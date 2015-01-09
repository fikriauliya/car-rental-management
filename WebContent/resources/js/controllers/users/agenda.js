myApp = angular.module('myApp');
myApp.controller('AgendaUserController', ['$scope', '$location', '$timeout', 'UserAgendas', '$filter', '$log', 'ngTableParams', 'ngProgress', 'TimezoneConverter',
  function($scope, $location, $timeout, UserAgendas, $filter, $log, ngTableParams, ngProgress, TimezoneConverter) {
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

	var tzdetect = {
	    names: moment.tz.names(),
	    matches: function(base){
	        var results = [], now = Date.now(), makekey = function(id){
	            return [0, 4, 8, -5*12, 4-5*12, 8-5*12, 4-2*12, 8-2*12].map(function(months){
	                var m = moment(now + months*30*24*60*60*1000);
	                if (id) m.tz(id);
	                return m.format("DDHHmm");
	            }).join(' ');
	        }, lockey = makekey(base);
	        tzdetect.names.forEach(function(id){
	            if (makekey(id)===lockey) results.push(id);
	        });
	        return results;
	    }
	};

	$scope.refreshEvents = function() {
		ngProgress.start();
		UserAgendas.query({id: $scope.userId, start: $scope.curStart, end: $scope.curEnd}, function(d, h){
			var digitsRegex = new RegExp("^(\\d+)\\-(\\d+)\\-(.+)$");

			if (d.length > 0) {
				$scope.myTimeZone = d[0].timezone;
			};

			_.each(d, function(dd) {
				if (digitsRegex.test(dd.title)) {
					var matches = digitsRegex.exec(dd.title);
					dd.titleLink = baseUrl + basePath + "/reservations/index.jsf#/" + matches[1] + "/reservations/" + matches[2];
					dd.title = matches[3];
				}
				dd.startTime = TimezoneConverter.convertToLocalTimeZoneTime(dd.start, $scope.myTimeZone);
        		dd.start = TimezoneConverter.convertToLocalTimeZoneTime(dd.start, $scope.myTimeZone);

        		dd.endTime = TimezoneConverter.convertToLocalTimeZoneTime(dd.end, $scope.myTimeZone);
        		dd.end = TimezoneConverter.convertToLocalTimeZoneTime(dd.end, $scope.myTimeZone);
			});
        	$scope.events[0] = [];
        	$scope.events[0] = d;
        	$scope.tableParams.reload();

        	ngProgress.complete();
        }, function(d, h) {
        	$scope.clearNotification();
			$scope.errors = d.data;

        	ngProgress.complete();
        });
	}
	$scope.initializeNewEvent = function() {
		$scope.newEvent = new UserAgendas();
		$scope.newEvent.start = date;
		$scope.newEvent.end = date;
	};

	$scope.createEvent = function() {
		ngProgress.start();
		var tzid = tzdetect.matches()[0];

		$scope.newEvent.timezone = tzid;

		$scope.newEvent.$save({id: $scope.userId}, function(d, h){
			$scope.clearNotification();

			$scope.refreshEvents();

			$scope.info = "Agenda " + $scope.newEvent.title + " created";
			$scope.initializeNewEvent();

			ngProgress.complete();
		}, function(d, h) {
			$scope.clearNotification();
			$scope.errors = d.data;

			ngProgress.complete();
		})
	};

	$scope.removeEvent = function(event) {
		ngProgress.start();
		UserAgendas.remove({eventId: event.id}, function(d, h) {
			$scope.info = "Agenda " + event.title + " removed";
			$scope.refreshEvents();
			ngProgress.complete();
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
        editable: false,
        header:{
          left: 'month agendaWeek agendaDay',
          center: 'title',
          right: 'today prev,next'
        },
        eventClick: $scope.alertOnEventClick,
        eventDrop: $scope.alertOnDrop,
        eventResize: $scope.alertOnResize,
        eventRender: $scope.eventRender,
        timezone: false,
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