myApp = angular.module('myApp');
myApp.controller('AgendaUserController', ['$scope', '$location', '$timeout', 'Users', '$filter', '$log', 'ngTableParams',
  function($scope, $location, $timeout, Users, $filter, $log, ngTableParams) {
	var date = new Date();
    var d = date.getDate();
    var m = date.getMonth();
    var y = date.getFullYear();

	$scope.userId = $location.search().userId;
	$scope.events= [[{title: 'All Day Event',start: new Date(y, m, 1), end: new Date(y, m, 3)}
		,{title: 'Long Event',start: new Date(y, m, d - 5),end: new Date(y, m, d - 2)}]];

	$scope.initializeNewEvent = function() {
		$scope.newEvent = {
			title: '',
			start: date,
			end: date
		};
	};

	$scope.createEvent = function() {
		$scope.events[0].push($scope.newEvent);
		$scope.initializeNewEvent();
		$scope.tableParams.reload();
	};

	$scope.removeEvent = function(index) {
		console.log("Remove event");
		$scope.events[0].splice(index, 1);
		$scope.tableParams.reload();
	}

	$scope.tableParams = new ngTableParams(
		{
		    page: 1, count: 10,
		    sorting: {
		        start: 'asc'
		    }
		},
		{
		    total: 0,
		    getData: function($defer, params) {
		        var orderedData = params.sorting() ? $filter('orderBy')($scope.events[0], params.orderBy()) : $scope.events[0];

		        params.total(orderedData.length);
		        $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
		    }
		});

    $scope.uiConfig = {
      calendar:{
        height: 450,
        editable: true,
        header:{
          left: 'month agendaWeek agendaDay',
          center: 'title',
          right: 'today prev,next'
        },
        eventClick: $scope.alertOnEventClick,
        eventDrop: $scope.alertOnDrop,
        eventResize: $scope.alertOnResize,
        eventRender: $scope.eventRender,
        viewRender: function(view, element) {
            $log.debug("View Changed: ", view.visStart, view.visEnd, view.start, view.end);
        }
      }
    };

    $scope.initializeNewEvent();
  }]
);