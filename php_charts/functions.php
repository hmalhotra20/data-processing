<?php
$param = $_REQUEST['q'];
if($param == "city")  {
  header('Content-Type: application/json');
  header("Access-Control-Allow-Origin: *");
  noCache();
  echo getCitiesData();
}
elseif ($param=="revenue") {
  header('Content-Type: application/json');
  header("Access-Control-Allow-Origin: *");
  noCache();
  echo getDailyMaxRevenue();
}

function getCitiesData()  {
  $dbhandle = new mysqli("127.0.0.1", "root", "", "simple_benchmark");
  $strQuery = "SELECT city, sum(total_amt) as total_amt FROM tbl3 GROUP BY  city order by total_amt desc LIMIT 10";
  $result = $dbhandle->query($strQuery) or exit("Error code ({$dbhandle->errno}): {$dbhandle->error}");
  $arrData = array();
  if ($result) {
      while($row = mysqli_fetch_object($result)) {
        array_push($arrData, array(
          "label"=> $row->city,
          "value"=> $row->total_amt
        ));
      }
  }
  $dbhandle->close();
  return json_encode($arrData);
}

function getDailyMaxRevenue()  {
  $dbhandle = new mysqli("127.0.0.1", "root", "", "simple_benchmark");
  $strQuery = "Select pdate, max(qty) qty, sum(amt) amt from tbl2 group by pdate order by pdate LIMIT 10";
  $result = $dbhandle->query($strQuery) or exit("Error code ({$dbhandle->errno}): {$dbhandle->error}");
  $arrData = array(); $pdate = array(); $qty = array(); $amt = array();
  if ($result) {
      while($obj = mysqli_fetch_object($result)) {
        $pdate[] = $obj->pdate;
        $qty[] = $obj->qty;
        $amt[] = $obj->amt;
      }
      $arrData['pdate'] = $pdate;
      $arrData['qty'] = $qty;
      $arrData['amt'] = $amt;
  }
  $dbhandle->close();
  return json_encode($arrData);
}

function barChartForCities()  {
  $data = getCitiesData();
  $data = json_decode($data);
  $arrData = array(
      "chart" => array(
        "caption" => "Top 10 Cities",
        "subCaption" => "Harry\’s SuperMart",
        "xAxisName" => "Month",
        "yAxisName" => "Revenues (In USD)",
        "numberPrefix" => "$",
        "theme" => "zune",
        "showValues" => "0",
        "theme" => "zune"
      )
    );
    $arrData["data"] = $data;
    $jsonEncodedData = json_encode($arrData);
    $columnChart = new FusionCharts("column2D", "ex21" , 600, 400, "chart-2", "json", $jsonEncodedData);
    $columnChart->render();
}

function pieChartieForCities()  {
  $data = getCitiesData();
  $data = json_decode($data);
  $arrData = array(
      "chart" => array(
          "caption" => "Top 10 Cities by sale",
          "subcaption" => "Day wise",
          "startingangle" => "120",
          "showlabels" => "0",
          "showlegend" => "1",
          "enablemultislicing" => "0",
          "slicingdistance" => "15",
          "showpercentvalues" => "1",
          "showpercentintooltip" => "1",
          "theme" => "fint"
        )
    );
    $arrData["data"] = $data;
    $jsonEncodedData = json_encode($arrData);
    $pie3dChart = new FusionCharts("pie3d", "ex2", 400, 400, "chart-1", "json", $jsonEncodedData);
    $pie3dChart->render();
}

function noCache()  {
  header("Cache-Control: no-store, no-cache, must-revalidate, max-age=0");
  header("Cache-Control: post-check=0, pre-check=0", false);
  header("Pragma: no-cache");
}

?>
