-- Tipos de pólizas
INSERT INTO policy_types (name, active)
VALUES
    ('INDIVIDUAL', TRUE),
    ('COLECTIVA', TRUE)
ON CONFLICT (name) DO NOTHING;


-- Estados de pólizas
INSERT INTO policy_statuses (name, active)
VALUES
    ('ACTIVA', TRUE),
    ('PENDIENTE', TRUE),
    ('CANCELADA', TRUE),
    ('EXPIRADA', TRUE),
    ('RENOVADA', TRUE)
ON CONFLICT (name) DO NOTHING;



-- ==========================================
-- 1. INSERTAR POLIZAS
-- ==========================================

INSERT INTO policies (
    policy_number,
    type_id,
    status_id,
    premium,
    rental_amount,
    start_date,
    end_date,
    description
)
VALUES
(
    'POL-2026-001',
    1,
    1,
    12000000.00,
    1000000.00,
    '2026-01-01',
    '2027-01-01',
    'Seguro individual de vivienda'
),
(
    'POL-2026-002',
    2,
    1,
    60000000.00,
    5000000.00,
    '2026-02-01',
    '2027-02-01',
    'Seguro colectivo empresarial'
),
(
    'POL-2026-003',
    1,
    2,
    30000000.00,
    2500000.00,
    '2026-03-01',
    '2027-03-01',
    'Seguro individual de vehículo'
);

-- ==========================================
-- 2. INSERTAR RIESGOS
-- ==========================================

INSERT INTO risks (
    name,
    description,
    insured_value,
    active,
    policy_id
)
VALUES

-- Riesgos de POL-2026-001
(
    'Incendio',
    'Daños materiales ocasionados por incendios en la vivienda',
    60000000.00,
    TRUE,
    (SELECT id FROM policies WHERE policy_number = 'POL-2026-001')
),

-- Riesgos de POL-2026-002
(
    'Responsabilidad civil',
    'Daños ocasionados a terceros por actividades empresariales',
    300000000.00,
    TRUE,
    (SELECT id FROM policies WHERE policy_number = 'POL-2026-002')
),
(
    'Daños materiales',
    'Daños a instalaciones, maquinaria y equipos empresariales',
    200000000.00,
    TRUE,
    (SELECT id FROM policies WHERE policy_number = 'POL-2026-002')
),

-- Riesgos de POL-2026-003
(
    'Accidentes de tránsito',
    'Daños ocasionados al vehículo por accidentes de tránsito',
    35000000.00,
    TRUE,
    (SELECT id FROM policies WHERE policy_number = 'POL-2026-003')
);
