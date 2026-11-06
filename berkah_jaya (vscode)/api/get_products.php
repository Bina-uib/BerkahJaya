
<?php
require_once "../config/database.php";

$result = $conn->query(
    "SELECT * FROM products ORDER BY id DESC"
);

$products = [];

while ($row = $result->fetch_assoc()) {
    $products[] = $row;
}

echo json_encode([
    "success" => true,
    "data" => $products
]);
?>
