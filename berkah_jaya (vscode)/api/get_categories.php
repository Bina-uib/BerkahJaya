<?php
require_once "../config/database.php";

$sql = "SELECT * FROM categories ORDER BY id DESC";
$result = $conn->query($sql);

$categories = [];

if ($result) {
    while ($row = $result->fetch_assoc()) {
        $categories[] = $row;
    }
}

echo json_encode([
    "success" => true,
    "data" => $categories
]);
?>
