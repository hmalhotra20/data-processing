<html>
  <head>
    <?php
       $page = $_SERVER['PHP_SELF']; $sec = "5";
    ?>
    <meta http-equiv="refresh" content="<?php echo $sec?>;URL='<?php echo $page?>'">
    <link href="node_modules/c3/c3.min.css" rel="stylesheet">
    <script type="text/javascript" src="node_modules/c3/c3.min.js"></script>
    <script type="text/javascript" src="node_modules/d3/d3.min.js"></script>
    <script type="text/javascript" src="node_modules/jquery/dist/jquery.min.js"></script>
    <script type="text/javascript">
    $( document ).ready(function() {
        drawArea();
    });
    function drawArea() {
      var chart = c3.generate({
          bindto: '#chart',
          data: {
              url: 'functions.php?q=revenue',
              mimeType: 'json',
              labels: true,
              names: {
                data1: 'Data Name 1',
                data2: 'Data Name 2',
                data2: 'Data Name 3'
              },
              groups: [
                ['qty', 'amt'],
                ['pdate']
              ],
              axes: {
                data1: 'x',
                data2: 'y2'
              },
              bar: {
                  width: {
                      ratio: 0.5
                  }
              },
              labels: true,
              type: 'area-spline'
          }
      });
    }
    </script>
  </head>
  <body>
    <div id="chart"></div>
  </body>
</html>
