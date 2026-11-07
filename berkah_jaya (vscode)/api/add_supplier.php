<?php
require_once "../config/database.php";

$nama = trim($_POST["nama_supplier"] ?? "");
$telepon = trim($_POST["telepon"] ?? "");
$alamat = trim($_POST["alamat"] ?? "");

if ($nama === "") {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "message" => "Nama supplier wajib diisi"
    ]);
    exit;
}

$stmt = $conn->prepare(
    "INSERT INTO suppliers
     (nama_supplier, telepon, alamat)
     VALUES (?, ?, ?)"
);

$stmt->bind_param("sss", $nama, $telepon, $alamat);

if ($stmt->execute()) {
    echo json_encode([
        "success" => true,
        "message" => "Supplier berhasil ditambahkan"
    ]);
} else {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "message" => "Supplier gagal ditambahkan"
    ]);
}

$stmt->close();
?>
