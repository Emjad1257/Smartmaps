from fastapi import FastAPI, Request, Depends
from fastapi.responses import FileResponse
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy import Column, String, Float, create_engine
from sqlalchemy.orm import sessionmaker, declarative_base, Session
from geopy.distance import geodesic
from ortools.constraint_solver import pywrapcp, routing_enums_pb2
import pandas as pd

# --- Database setup ---
DATABASE_URL = "mysql+pymysql://root:@127.0.0.1:3306/base_client_xasa"

engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(bind=engine, autoflush=False, autocommit=False)
Base = declarative_base()
app = FastAPI()


# --- SQLAlchemy model ---
class Client(Base):
    __tablename__ = "base_client_xasa"
    uprole_code = Column(String)
    UPGEOAREA_CODE = Column(String)
    GEOAREA_CODE = Column(String)
    ITINERARY_CODE = Column(String)
    PARTNER_CODE = Column(String, primary_key=True)
    NAME = Column(String)
    LONGITUDE = Column(Float)
    LATITUDE = Column(Float)


# --- FastAPI app setup ---

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


# --- Sidebar data endpoint ---
@app.get("/sidebar-data")
def get_sidebar_data(db: Session = Depends(get_db)):
    clients = db.query(Client).all()
    hierarchy = {}

    for c in clients:
        upgeo = c.UPGEOAREA_CODE
        geo = c.GEOAREA_CODE
        itinerary = c.ITINERARY_CODE

        if upgeo not in hierarchy:
            hierarchy[upgeo] = {
                "upgeoId": upgeo,
                "geoareas": {}
            }

        upgeo_node = hierarchy[upgeo]

        if geo not in upgeo_node["geoareas"]:
            upgeo_node["geoareas"][geo] = {
                "geoId": geo,
                "itineraries": {}
            }

        geo_node = upgeo_node["geoareas"][geo]

        if itinerary not in geo_node["itineraries"]:
            geo_node["itineraries"][itinerary] = {
                "itineraryId": itinerary,
                "clients": []
            }

        geo_node["itineraries"][itinerary]["clients"].append({
            "id": c.PARTNER_CODE,
            "name": c.NAME,
            "uprole_code": c.uprole_code,
            "lat": c.LATITUDE,
            "lon": c.LONGITUDE
        })

    # Convert nested dicts to lists
    result = []
    for upgeo in hierarchy.values():
        upgeo["geoareas"] = list(upgeo["geoareas"].values())
        for geo in upgeo["geoareas"]:
            geo["itineraries"] = list(geo["itineraries"].values())
        result.append(upgeo)

    return result

# --- Route optimization ---
def optimize_route(depot_coords, clients):
    locs = [depot_coords] + [(c['lat'], c['lon']) for c in clients]

    dist_matrix_km = [
        [round(geodesic(a, b).km, 2) for b in locs]
        for a in locs
    ]

    manager = pywrapcp.RoutingIndexManager(len(locs), 1, 0)
    routing = pywrapcp.RoutingModel(manager)

    def distance_callback(from_index, to_index):
        from_node = manager.IndexToNode(from_index)
        to_node = manager.IndexToNode(to_index)
        return int(dist_matrix_km[from_node][to_node] * 1000)

    transit_callback = routing.RegisterTransitCallback(distance_callback)
    routing.SetArcCostEvaluatorOfAllVehicles(transit_callback)

    search_params = pywrapcp.DefaultRoutingSearchParameters()
    search_params.first_solution_strategy = routing_enums_pb2.FirstSolutionStrategy.PATH_CHEAPEST_ARC
    solution = routing.SolveWithParameters(search_params)

    ordered_clients = []
    cumulative_km = 0.0
    previous_node = 0
    index = routing.Start(0)

    while not routing.IsEnd(index):
        node = manager.IndexToNode(index)
        if node > 0:
            client = clients[node - 1]
            step_distance = dist_matrix_km[previous_node][node]
            cumulative_km += step_distance

            client["DistanceFromPreviousKM"] = round(step_distance, 2)
            client["CumulativeDistanceKM"] = round(cumulative_km, 2)
            ordered_clients.append(client)

            previous_node = node
        index = solution.Value(routing.NextVar(index))

    return ordered_clients

# --- Optimization endpoint ---
@app.post("/optimize-visit/")
async def optimize_visite(request: Request):
    payload = await request.json()
    depot_coords = (payload["lat"], payload["lon"])

    all_clients = []

    if "client" in payload:
        all_clients = [payload["client"]]
    elif "itinerary" in payload:
        all_clients = payload["itinerary"].get("clients", [])
    elif "geo" in payload:
        for itinerary in payload["geo"].get("itineraries", []):
            all_clients += itinerary.get("clients", [])
    elif "upgeo" in payload:
        for geo in payload["upgeo"].get("geoareas", []):
            for itinerary in geo.get("itineraries", []):
                all_clients += itinerary.get("clients", [])
    elif "allDepots" in payload and "depots" in payload:
        for depot in payload["depots"]:
            for role in depot.get("roles", []):
                for tournee in role.get("tournees", []):
                    all_clients += tournee.get("clients", [])

    valid_clients = [
        c for c in all_clients
        if c.get("lat") is not None and c.get("lon") is not None
    ]

    if not valid_clients:
        return {"error": "No valid clients with coordinates found"}

    sorted_clients = optimize_route(depot_coords, valid_clients)

    df = pd.DataFrame(sorted_clients)
    df.index = range(1, len(df) + 1)
    df.index.name = "VisitOrder"
    file_path = "visit_plan.xlsx"
    df.to_excel(file_path)

    return FileResponse(
        path=file_path,
        filename="visit_plan.xlsx",
        media_type="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )

