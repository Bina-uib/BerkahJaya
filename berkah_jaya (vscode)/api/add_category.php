<?php
require_once "../config/database.php";

$nama = trim($_POST["nama_kategori"] ?? "");

if ($nama === "") {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "message" => "Nama kategori wajib diisi"
    ]);
    exit;
}

$stmt = $conn->prepare(
    "INSERT INTO categories (nama_kategori) VALUES (?)"
);
$stmt->bind_param("s", $nama);

if ($stmt->execute()) {
    echo json_encode([
        "success" => true,
        "message" => "Kategori berhasil ditambahkan"
    ]);
} else {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "message" => "Kategori gagal ditambahkan"
    ]);
}

$stmt->close();
?>