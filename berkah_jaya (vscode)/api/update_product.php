<?php
require_once "../config/database.php";

$id = $_POST["id"] ?? "";
$nama = $_POST["nama_produk"] ?? "";
$harga = $_POST["harga"] ?? "";
$stok = $_POST["stok"] ?? "";

if (!filter_var($id, FILTER_VALIDATE_INT) || $id < 1 ||
    $nama === "" || !is_numeric($harga) ||
    !is_numeric($stok) || $harga < 0 ||
    $stok < 0 || (int)$stok != $stok) {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "message" => "Input tidak valid"
    ]);
    exit;
}

$stmt = $conn->prepare(
    "UPDATE products SET nama_produk=?, harga=?, stok=? WHERE id=?"
);

$stmt->bind_param("sdii", $nama, $harga, $stok, $id);
$stmt->execute();

echo json_encode([
    "success" => true,
    "message" => "Proses update selesai"
]);
?>
