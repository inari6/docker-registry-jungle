$(document).ready(function() {

    $.ajax({
        url: "http://localhost:8080/graph"
    }).then(function(data) {
      var g = Viz(data);
      document.body.innerHTML += g;

      // Optional - resize the SVG element based on the contents.
      var svg = document.querySelector('#graphContainer');
      var bbox = svg.getBBox();
      svg.style.width = bbox.width + 40.0 + "px";
      svg.style.height = bbox.height + 40.0 + "px";
    });
});