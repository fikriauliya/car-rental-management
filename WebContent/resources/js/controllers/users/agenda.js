myApp = angular.module('myApp');
myApp.controller('AgendaUserController', ['$scope', '$location', '$timeout', 'UserAgendas', '$filter', '$log', 'ngTableParams',
  function($scope, $location, $timeout, UserAgendas, $filter, $log, ngTableParams) {
	var date = new Date();
    var d = date.getDate();
    var m = date.getMonth();
    var y = date.getFullYear();

	$scope.userId = $location.search().userId;
	$scope.events = [[]];

	$scope.clearNotification = function() {
		$scope.errors = [];
		$scope.info = "";
	};

	$scope.refreshEvents = function() {
		UserAgendas.query({id: $scope.userId, start: $scope.curStart, end: $scope.curEnd}, function(d, h){
        	$scope.events[0] = [];
        	$scope.events[0] = d;
        	$scope.tableParams.reload();
        }, function(d, h) {
        });
	}
	$scope.initializeNewEvent = function() {
		$scope.newEvent = new UserAgendas();
		$scope.newEvent.start = date;
		$scope.newEvent.end = date;
	};

	$scope.createEvent = function() {
		$scope.newEvent.$save({id: $scope.userId}, function(d, h){
			$scope.clearNotification();

			$scope.refreshEvents();

			$scope.info = "Agenda " + $scope.newEvent.title + " created";
			$scope.initializeNewEvent();
		}, function(d, h) {
			$scope.clearNotification();
			$scope.errors = d.data;
		})
	};

	$scope.removeEvent = function(event) {
		UserAgendas.remove({eventId: event.id}, function(d, h) {
			$scope.info = "Agenda " + event.title + " removed";
			$scope.refreshEvents();
		});
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
            var start = (new Date(view.start._d)).getTime();
            var end = (new Date(view.end._d)).getTime();

            $scope.curStart = start;
            $scope.curEnd = end;

            $("#myCalendar").fullCalendar('removeEvents');
            $scope.refreshEvents();
        }
      }
    };

    $scope.initializeNewEvent();
  }]
);