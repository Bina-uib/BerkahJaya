<?php
require_once "../config/database.php";

$sql = "SELECT * FROM suppliers ORDER BY id DESC";
$result = $conn->query($sql);

$suppliers = [];

if ($result) {
    while ($row = $result->fetch_assoc()) {
        $suppliers[] = $row;
    }
}

echo json_encode([
    "success" => true,
    "data" => $suppliers
]);
?>
