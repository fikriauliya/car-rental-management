myApp = angular.module('myApp', ['userServices', 'organizationServices', 'peerReviewServices',
                                 'angularBootstrapNavTree', 'transferLogsServices', 'angularSpinner', 'ngTable']);
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

myApp.config(function($provide, $httpProvider) {
	$httpProvider.interceptors.push('myHttpInterceptor');
});