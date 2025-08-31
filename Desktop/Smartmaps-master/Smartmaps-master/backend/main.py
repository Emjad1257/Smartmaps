from __future__ import annotations

import os
from typing import Any, Dict, List, Tuple

from fastapi import FastAPI, Request, Depends, HTTPException, BackgroundTasks
from fastapi.responses import FileResponse
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field

from sqlalchemy import Column, String, Float, create_engine, text
from sqlalchemy.orm import sessionmaker, declarative_base, Session

from geopy.distance import geodesic
from ortools.constraint_solver import pywrapcp, routing_enums_pb2

import pandas as pd

# ============================================================
# Config
# ============================================================

# URL DB : sur MySQL par défaut ; tu peux la surcharger par variable d'env DATABASE_URL
# Exemple MySQL : mysql+pymysql://user:password@127.0.0.1:3306/base_client_xasa
# Exemple SQLite : sqlite:///./local.db
DATABASE_URL = os.getenv(
    "DATABASE_URL",
    "mysql+pymysql://root:@127.0.0.1:3306/base_client_xasa"
)

ALLOWED_ORIGINS = os.getenv(
    "ALLOWED_ORIGINS",
    "http://localhost:4200,http://127.0.0.1:4200"
).split(",")

EXCEL_FILE = "visit_plan.xlsx"

# ============================================================
# App + CORS
# ============================================================

app = FastAPI(title="SmartMaps Backend", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=ALLOWED_ORIGINS,   # en dev tu peux mettre ["*"]
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ============================================================
# SQLAlchemy
# ============================================================

is_sqlite = DATABASE_URL.startswith("sqlite")
connect_args = {"check_same_thread": False} if is_sqlite else {}

engine = create_engine(
    DATABASE_URL,
    pool_pre_ping=True,
    echo=False,
    future=True,
    connect_args=connect_args
)

SessionLocal = sessionmaker(
    bind=engine,
    autoflush=False,
    autocommit=False,
    expire_on_commit=False
)

Base = declarative_base()

def get_db() -> Session:
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# ============================================================
# Modèle
# ============================================================

class Client(Base):
    __tablename__ = "base_client_xasa"

    # adapte si besoin les tailles/nullable
    uprole_code     = Column(String)
    UPGEOAREA_CODE  = Column(String)
    GEOAREA_CODE    = Column(String)
    ITINERARY_CODE  = Column(String)
    PARTNER_CODE    = Column(String, primary_key=True)
    NAME            = Column(String)
    LONGITUDE       = Column(Float)
    LATITUDE        = Column(Float)

# Si tu utilises SQLite en dev et que la table n'existe pas :
# if is_sqlite:
#     Base.metadata.create_all(engine)

# ============================================================
# Schemas
# ============================================================

class LocationUpdate(BaseModel):
    lat: float = Field(..., ge=-90, le=90)
    lon: float = Field(..., ge=-180, le=180)

# ============================================================
# Utils OR-Tools
# ============================================================

def build_distance_matrix_km(coords: List[Tuple[float, float]]) -> List[List[float]]:
    return [
        [round(geodesic(a, b).km, 2) for b in coords]
        for a in coords
    ]

def optimize_route(
    depot_coords: Tuple[float, float],
    clients: List[Dict[str, Any]]
) -> List[Dict[str, Any]] | None:
    if len(clients) < 1:
        return []

    locs = [depot_coords] + [(c["lat"], c["lon"]) for c in clients]
    dist_matrix_km = build_distance_matrix_km(locs)

    manager = pywrapcp.RoutingIndexManager(len(locs), 1, 0)  # 1 véhicule, dépôt index 0
    routing = pywrapcp.RoutingModel(manager)

    def distance_callback(from_index, to_index):
        f = manager.IndexToNode(from_index)
        t = manager.IndexToNode(to_index)
        return int(dist_matrix_km[f][t] * 1000)  # m

    transit_callback = routing.RegisterTransitCallback(distance_callback)
    routing.SetArcCostEvaluatorOfAllVehicles(transit_callback)

    params = pywrapcp.DefaultRoutingSearchParameters()
    params.first_solution_strategy = routing_enums_pb2.FirstSolutionStrategy.PATH_CHEAPEST_ARC

    solution = routing.SolveWithParameters(params)
    if solution is None:
        return None

    ordered: List[Dict[str, Any]] = []
    cumulative_km = 0.0
    prev_node = 0
    index = routing.Start(0)

    while not routing.IsEnd(index):
        node = manager.IndexToNode(index)
        if node > 0:
            client = dict(clients[node - 1])
            step = dist_matrix_km[prev_node][node]
            cumulative_km += step
            client["DistanceFromPreviousKM"] = round(step, 2)
            client["CumulativeDistanceKM"] = round(cumulative_km, 2)
            ordered.append(client)
            prev_node = node
        index = solution.Value(routing.NextVar(index))

    return ordered

# ============================================================
# Endpoints
# ============================================================

@app.get("/health")
def health() -> Dict[str, str]:
    return {"status": "ok"}

@app.get("/health-db")
def health_db(db: Session = Depends(get_db)) -> Dict[str, str]:
    db.execute(text("SELECT 1"))
    return {"db": "ok"}

@app.get("/sidebar-data")
def get_sidebar_data(db: Session = Depends(get_db)) -> List[Dict[str, Any]]:
    """
    Regroupe les clients en hiérarchie:
    upgeo -> geo -> itineraries -> clients
    """
    clients = db.query(Client).all()
    hierarchy: Dict[str, Dict[str, Any]] = {}

    for c in clients:
        upgeo = c.UPGEOAREA_CODE or "UNKNOWN_UPGEO"
        geo   = c.GEOAREA_CODE    or "UNKNOWN_GEO"
        itin  = c.ITINERARY_CODE  or "UNKNOWN_ITIN"

        if upgeo not in hierarchy:
            hierarchy[upgeo] = {"upgeoId": upgeo, "geoareas": {}}
        up = hierarchy[upgeo]

        if geo not in up["geoareas"]:
            up["geoareas"][geo] = {"geoId": geo, "itineraries": {}}
        g = up["geoareas"][geo]

        if itin not in g["itineraries"]:
            g["itineraries"][itin] = {"itineraryId": itin, "clients": []}

        g["itineraries"][itin]["clients"].append({
            "id": c.PARTNER_CODE,
            "name": c.NAME,
            "uprole_code": c.uprole_code,
            "lat": c.LATITUDE,
            "lon": c.LONGITUDE,
        })

    # dict -> listes
    result: List[Dict[str, Any]] = []
    for up in hierarchy.values():
        up["geoareas"] = list(up["geoareas"].values())
        for g in up["geoareas"]:
            g["itineraries"] = list(g["itineraries"].values())
        result.append(up)

    return result

@app.put("/clients/{partner_code}/location")
def update_client_location(
    partner_code: str,
    loc: LocationUpdate,
    db: Session = Depends(get_db)
) -> Dict[str, Any]:
    """
    Met à jour la latitude/longitude d'un client identifié par PARTNER_CODE.
    """
    client = db.query(Client).filter(Client.PARTNER_CODE == partner_code).first()
    if client is None:
        raise HTTPException(status_code=404, detail="Client not found")

    client.LATITUDE = float(loc.lat)
    client.LONGITUDE = float(loc.lon)
    db.commit()

    return {
        "id": client.PARTNER_CODE,
        "name": client.NAME,
        "uprole_code": client.uprole_code,
        "lat": client.LATITUDE,
        "lon": client.LONGITUDE,
        "message": "Location updated"
    }

@app.post("/optimize-visit/")
async def optimize_visite(request: Request, bg: BackgroundTasks):
    """
    Corps JSON possible :
    {
      "lat": 33.58, "lon": -7.61,
      "client": {...} | "itinerary": {...} | "geo": {...} | "upgeo": {...} |
      "allDepots": true, "depots": [...]
    }
    """
    payload = await request.json()
    depot_coords = (payload["lat"], payload["lon"])

    # Collecte des clients selon la structure fournie
    all_clients: List[Dict[str, Any]] = []
    if "client" in payload:
        all_clients = [payload["client"]]
    elif "itinerary" in payload:
        all_clients = payload["itinerary"].get("clients", [])
    elif "geo" in payload:
        for itin in payload["geo"].get("itineraries", []):
            all_clients += itin.get("clients", [])
    elif "upgeo" in payload:
        for geo in payload["upgeo"].get("geoareas", []):
            for itin in geo.get("itineraries", []):
                all_clients += itin.get("clients", [])
    elif payload.get("allDepots") and "depots" in payload:
        for depot in payload["depots"]:
            for role in depot.get("roles", []):
                for tournee in role.get("tournees", []):
                    all_clients += tournee.get("clients", [])

    valid_clients = [c for c in all_clients if c.get("lat") is not None and c.get("lon") is not None]
    if not valid_clients:
        raise HTTPException(status_code=400, detail="No valid clients with coordinates found")

    result = optimize_route(depot_coords, valid_clients)
    if result is None:
        raise HTTPException(status_code=422, detail="No feasible route found")

    if len(result) == 0:
        return {"message": "Nothing to optimize", "clients": valid_clients}

    # Excel (openpyxl requis)
    df = pd.DataFrame(result)
    df.index = range(1, len(df) + 1)
    df.index.name = "VisitOrder"
    df.to_excel(EXCEL_FILE, index=True)

    # suppression du fichier après envoi
    def _cleanup(path: str):
        try:
            if os.path.exists(path):
                os.remove(path)
        except Exception:
            pass

    bg.add_task(_cleanup, EXCEL_FILE)

    return FileResponse(
        path=EXCEL_FILE,
        filename="visit_plan.xlsx",
        media_type="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    )
