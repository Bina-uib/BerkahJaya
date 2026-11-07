<?php
header('Content-Type: application/json');
require_once __DIR__ . '/../config/database.php';

// ambil parameter dari URL (semua boleh kosong)
$q         = $_GET['q'] ?? '';
$min_stok  = $_GET['min_stok'] ?? '';
$max_stok  = $_GET['max_stok'] ?? '';

$sql    = "SELECT * FROM products WHERE 1=1";
$types  = '';
$params = [];

if ($q !== '') {
    $sql .= " AND nama_produk LIKE ?";
    $types .= 's';
    $params[] = "%$q%";
}
if ($min_stok !== '') {
    $sql .= " AND stok >= ?";
    $types .= 'i';
    $params[] = (int)$min_stok;
}
if ($max_stok !== '') {
    $sql .= " AND stok <= ?";
    $types .= 'i';
    $params[] = (int)$max_stok;
}

$sql .= " ORDER BY nama_produk ASC";

$stmt = $conn->prepare($sql);
if ($types !== '') {
    $stmt->bind_param($types, ...$params);
}
$stmt->execute();
$result = $stmt->get_result();

$data = [];
while ($row = $result->fetch_assoc()) {
    $data[] = $row;
}

echo json_encode($data);