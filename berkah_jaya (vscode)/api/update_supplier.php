<?php
require_once "../config/database.php";

$id = $_POST["id"] ?? "";
$nama = trim($_POST["nama_supplier"] ?? "");
$telepon = trim($_POST["telepon"] ?? "");
$alamat = trim($_POST["alamat"] ?? "");

if (
    !filter_var($id, FILTER_VALIDATE_INT) ||
    $id < 1 ||
    $nama === ""
) {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "message" => "Data supplier tidak valid"
    ]);
    exit;
}

$stmt = $conn->prepare(
    "UPDATE suppliers
     SET nama_supplier = ?,
         telepon = ?,
         alamat = ?
     WHERE id = ?"
);

$stmt->bind_param(
    "sssi",
    $nama,
    $telepon,
    $alamat,
    $id
);

if ($stmt->execute()) {
    echo json_encode([
        "success" => true,
        "message" => $stmt->affected_rows >= 0
            ? "Supplier berhasil diperbarui"
            : "Proses update selesai"
    ]);
} else {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "message" => "Gagal memperbarui supplier"
    ]);
}

$stmt->close();
?>
