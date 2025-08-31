export interface Client {
  id: string;
  name: string;
  lat: number;
  lon: number;
}

export interface Tournee {
  tourneeId: string;
  tourneeName: string;
  clients: Client[];
}

export interface Role {
  roleId: string;
  roleName: string;
  tournees: Tournee[];
}

export interface Depot {
  depotId: string;
  depotName: string;
  lat: number;
  lon: number;
  roles: Role[];
}

export const data: Depot[] = [
  {
    depotId: 'depot01',
    depotName: 'Depot 01',
    lat: 33.5500,
    lon: -7.5900,
    roles: [
      {
        roleId: 'role01',
        roleName: 'Role 01',
        tournees: [
          {
            tourneeId: 'tournee01',
            tourneeName: 'Tournee 01',
            clients: [
              { id: 'client01', name: 'Zakaria Hamidid', lat: 33.5731, lon: -7.5898 },
              { id: 'client02', name: 'Ghizlane Abdmoumen', lat: 33.574, lon: -7.58 },
              { id: 'client03', name: 'Hamza Aoulaui ', lat: 33.57, lon: -7.59 },
              { id: 'client04', name: 'Karima Hakim', lat: 33.575, lon: -7.582 },
              { id: 'client05', name: 'Hakim Bchiri', lat: 33.576, lon: -7.583 },
            ],
          },
        ],
      },
    ],
  },
  {
    depotId: 'depot02',
    depotName: 'Depot 02',
    lat: 33.5925,
    lon: -7.5995,
    roles: [
      {
        roleId: 'role01',
        roleName: 'Role 01',
        tournees: [
          {
            tourneeId: 'tournee01',
            tourneeName: 'Tournee 01',
            clients: [
              { id: 'client06', name: 'Latifa Hasim', lat: 33.589, lon: -7.596 },
              { id: 'client07', name: 'Darif kadim', lat: 33.590, lon: -7.597 },
              { id: 'client08', name: 'Khadija latif', lat: 33.591, lon: -7.598 },
              { id: 'client09', name: 'Aymen Ait Mansour ', lat: 33.592, lon: -7.599 },
            ],
          },
        ],
      },
      {
        roleId: 'role02',
        roleName: 'Role 02',
        tournees: [
          {
            tourneeId: 'tournee02',
            tourneeName: 'Tournee 02',
            clients: [
              { id: 'client10', name: 'Rachida jadid', lat: 33.593, lon: -7.600 },
              { id: 'client11', name: 'Malika Gaziz', lat: 33.594, lon: -7.601 },
              { id: 'client12', name: 'Fatima Zahra Chams', lat: 33.595, lon: -7.602 },
              { id: 'client13', name: 'kamal liftiz', lat: 33.596, lon: -7.603 },
            ],
          },
        ],
      },
    ],
  },
  {
    depotId: 'depot03',
    depotName: 'Depot03',
    lat: 33.5550,
    lon: -7.6780,
    roles: [
      {
        roleId: 'role04',
        roleName: 'Role 01',
        tournees: [
          {
            tourneeId: 'tournee05',
            tourneeName: 'Tournee 01',
            clients: [
              { id: 'client10', name: 'Sanae Idrissi', lat: 33.5551, lon: -7.6780 },
              { id: 'client11', name: 'Rami Berrada', lat: 33.5545, lon: -7.6785 },
              { id: 'client12', name: 'Khadija L.', lat: 33.5540, lon: -7.6770 },
              { id: 'client13', name: 'Youssef Chami', lat: 33.5553, lon: -7.6791 },
              { id: 'client14', name: 'Nada El A.', lat: 33.5560, lon: -7.6782 },
            ],
          },
        ],
      },
      {
        roleId: 'role05',
        roleName: 'Role 02',
        tournees: [
          {
            tourneeId: 'tournee02',
            tourneeName: 'Tournee 02',
            clients: [
              { id: 'client15', name: 'Meryem Ouali', lat: 33.5565, lon: -7.6784 },
              { id: 'client16', name: 'Zakaria H.', lat: 33.5557, lon: -7.6778 },
              { id: 'client17', name: 'Hanae Slimani', lat: 33.5550, lon: -7.6769 },
              { id: 'client18', name: 'Nabil Karroumi', lat: 33.5543, lon: -7.6786 },
            ],
          },
        ],
      },
      {
        roleId: 'role06',
        roleName: 'Role 03',
        tournees: [
          {
            tourneeId: 'tournee03',
            tourneeName: 'Tournee 03',
            clients: [
              { id: 'client19', name: 'Siham Bennani', lat: 33.5538, lon: -7.6790 },
              { id: 'client20', name: 'Walid El Fassi', lat: 33.5534, lon: -7.6780 },
              { id: 'client21', name: 'Aya Bouhaddou', lat: 33.5542, lon: -7.6794 },
              { id: 'client22', name: 'Reda Idrissi', lat: 33.5547, lon: -7.6777 },
            ],
          },
        ],
      },
      {
        roleId: 'role07',
        roleName: 'Role 04',
        tournees: [
          {
            tourneeId: 'tournee04',
            tourneeName: 'Tournee 04',
            clients: [
              { id: 'client23', name: 'Imane Najdi', lat: 33.5559, lon: -7.6791 },
              { id: 'client24', name: 'Adil Z.', lat: 33.5563, lon: -7.6788 },
              { id: 'client25', name: 'Malak Chaoui', lat: 33.5560, lon: -7.6779 },
              { id: 'client26', name: 'Taha Louafi', lat: 33.5554, lon: -7.6786 },
            ],
          },
        ],
      },
      {
        roleId: 'role08',
        roleName: 'Role 05',
        tournees: [
          {
            tourneeId: 'tournee05',
            tourneeName: 'Tournee 05',
            clients: [
              { id: 'client27', name: 'Sara Y.', lat: 33.5562, lon: -7.6792 },
              { id: 'client28', name: 'Yassine O.', lat: 33.5551, lon: -7.6776 },
              { id: 'client29', name: 'Ikram L.', lat: 33.5544, lon: -7.6768 },
              { id: 'client30', name: 'Jalil D.', lat: 33.5539, lon: -7.6774 },
            ],
          },
        ],
      },
    ],
  },
  {
    depotId: 'depot04',
  depotName: 'Depot04',
  lat: 33.5820,
    lon: -7.5240,
  roles: [
    {
      roleId: 'role01',
      roleName: 'Role 01',
      tournees: [
        {
          tourneeId: 'tournee01',
          tourneeName: 'Tournee 01',
          clients: [
            { id: 'client32', name: 'Fatima Zahra', lat: 33.5820, lon: -7.5240 },
            { id: 'client35', name: 'Omar El Fassi', lat: 33.5825, lon: -7.5235 },
            { id: 'client40', name: 'Karima M.', lat: 33.5810, lon: -7.5230 },
            { id: 'client41', name: 'Anas Bouzid', lat: 33.5805, lon: -7.5245 },
            { id: 'client42', name: 'Soukaina Ben', lat: 33.5818, lon: -7.5250 },
          ],
        },
      ],
    },
    {
      roleId: 'role02',
      roleName: 'Role 02',
      tournees: [
        {
          tourneeId: 'tournee02',
          tourneeName: 'Tournee 02',
          clients: [
            { id: 'client43', name: 'Mehdi Rami', lat: 33.5830, lon: -7.5225 },
            { id: 'client44', name: 'Yasmine A.', lat: 33.5840, lon: -7.5242 },
            { id: 'client45', name: 'Rachid Amine', lat: 33.5827, lon: -7.5260 },
            { id: 'client46', name: 'Noura Chafik', lat: 33.5812, lon: -7.5220 },
          ],
        },
      ],
    },
    {
      roleId: 'role03',
      roleName: 'Role 03',
      tournees: [
        {
          tourneeId: 'tournee03',
          tourneeName: 'Tournee 03',
          clients: [
            { id: 'client47', name: 'Zineb Talbi', lat: 33.5807, lon: -7.5236 },
            { id: 'client48', name: 'Hamza Belkadi', lat: 33.5803, lon: -7.5248 },
            { id: 'client49', name: 'Ilham S.', lat: 33.5815, lon: -7.5255 },
            { id: 'client50', name: 'Abdelali H.', lat: 33.5823, lon: -7.5262 },
          ],
        },
      ],
    },
  ],
}

];
