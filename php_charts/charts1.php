<!doctype html>
<?php
   include("fusioncharts.php"); include("functions.php");
   $page = $_SERVER['PHP_SELF']; $sec = "5";
   $dbhandle = new mysqli("127.0.0.1", "root", "", "simple_benchmark");
?>
<html lang="en">
  	<title>Petrol Pumps</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta http-equiv="refresh" content="<?php echo $sec?>;URL='<?php echo $page?>'">
    <link rel="stylesheet" href="node_modules/bootstrap/dist/css/bootstrap.min.css">
  	<script type="text/javascript" src="node_modules/fusioncharts/fusioncharts.js"></script>
    <script src="node_modules/bootstrap/dist/js/bootstrap.min.js" crossorigin="anonymous"></script>
  </head>
   <body>
  	<?php
      pieChartieForCities($arrData['data']);
      barChartForCities();
  	?>
    <div class="container">
      <div class="row">
        <div class="col-md-6">
            <h1>Top Cities through Pie</h1>
            <div id="chart-1"></div>
        </div>
        <div class="col-md-6">
          <h1>Top Cities through Bar</h1>
          <div id="chart-2"></div>
        </div>
      </div>
  </div>

   </body>
</html>
