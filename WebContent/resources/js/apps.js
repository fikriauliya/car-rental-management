myApp = angular.module('myApp', ['userServices', 'organizationServices', 'peerReviewServices', 'userAgendaServices',
                                 'angularBootstrapNavTree', 'transferLogsServices', 'angularSpinner',
                                 'ngTable', 'ui.calendar', 'ui.bootstrap.datetimepicker']);
myApp.factory('myHttpInterceptor', ['$q', function($q) {
	  return {
	    'response': function(response) {
	      return response || $q.when(response);
	    },

	   'responseError': function(rejection) {
		  console.log("Error");
		  console.log(rejection);
		  switch (rejection.status) {
		  	case 401:
		  		window.location = basePath + "/login.jsf";
		  		break;
		  }
	      return $q.reject(rejection);
	    }
	  };
}]);

myApp.config(function($provide, $httpProvider, $locationProvider) {
	$httpProvider.interceptors.push('myHttpInterceptor');
	$locationProvider.html5Mode(true);
});
