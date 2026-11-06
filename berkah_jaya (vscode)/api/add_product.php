
<?php
require_once "../config/database.php";

$nama = $_POST["nama_produk"] ?? "";
$harga = $_POST["harga"] ?? "";
$stok = $_POST["stok"] ?? "";

if ($nama === "" || !is_numeric($harga) ||
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
    "INSERT INTO products (nama_produk, harga, stok)
    VALUES (?, ?, ?)"
);

$stmt->bind_param("sdi", $nama, $harga, $stok);

if ($stmt->execute()) {
    echo json_encode([
        "success" => true,
        "message" => "Produk berhasil ditambahkan"
    ]);
} else {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "message" => "Gagal menambahkan produk"
    ]);
}
?>
