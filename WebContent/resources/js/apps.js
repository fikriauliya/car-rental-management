var httpInterceptor = function($q) {
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
	};

var myApp = angular.module('myApp', ['userServices', 'organizationServices', 'peerReviewServices', 'userAgendaServices',
                                 'angularBootstrapNavTree', 'transferLogsServices', 'angularSpinner',
                                 'ngTable', 'ui.calendar', 'ui.bootstrap.datetimepicker', 'ngProgress']);
myApp.factory('myHttpInterceptor', ['$q', httpInterceptor]);

myApp.config(function($provide, $httpProvider, $locationProvider) {
	$httpProvider.interceptors.push('myHttpInterceptor');
	$locationProvider.html5Mode(true);
});


var branchManagementApp = angular.module('branchManagementApp', ['branchServices', 'searchServices', 'ngTable', 'ui.calendar', 'ngProgress', 'ui.router']);
branchManagementApp.factory('myHttpInterceptor', ['$q', httpInterceptor]);

branchManagementApp.config(function($provide, $httpProvider, $locationProvider) {
	$httpProvider.interceptors.push('myHttpInterceptor');
	$locationProvider.html5Mode(false);
});

branchManagementApp.run(['$rootScope', '$log', function($rootScope, $log) {
	$rootScope.$on('$stateChangeStart',
		function(event, toState, toParams, fromState, fromParams){
			$log.log(fromState, " -> ", toState);
		}
	);
}]);

branchManagementApp.config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {
	$urlRouterProvider.otherwise("");
	$stateProvider
		.state('branch', {
			url: "",
			views: {
				"default": {
					templateUrl: 'partials/branch-list.xhtml',
					controller: 'IndexBranchController'
				}
			}
		})
		.state('branch.members', {
			url: '/members/:id',
			views: {
				"default": {
					templateUrl: 'partials/member-list.xhtml',
					controller: 'IndexBranchMemberController'
				},
				"createBranch": {
					templateUrl: 'partials/branch-create.xhtml',
					controller: 'CreateBranchController'
				}
			}
		})
}]);

var inventoryManagementApp = angular.module('inventoryManagementApp', ['branchServices', 'inventoryServices', 'ngTable', 'ui.calendar', 'ngProgress', 'ui.router']);
inventoryManagementApp.factory('myHttpInterceptor', ['$q', httpInterceptor]);

inventoryManagementApp.config(function($provide, $httpProvider, $locationProvider) {
	$httpProvider.interceptors.push('myHttpInterceptor');
	$locationProvider.html5Mode(false);
});

inventoryManagementApp.run(['$rootScope', '$log', function($rootScope, $log) {
	$rootScope.$on('$stateChangeStart',
		function(event, toState, toParams, fromState, fromParams){
			$log.log(fromState, " -> ", toState);
		}
	);
}]);

inventoryManagementApp.config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {
	$urlRouterProvider.otherwise("");
	$stateProvider
		.state('branch', {
			url: "",
			views: {
				"default": {
					templateUrl: '../branches/partials/branch-list.xhtml',
					controller: 'IndexBranchController'
				}
			}
		})
		.state('branch.members', {
			url: '/members/:id',
			views: {
				"default": {
					templateUrl: 'partials/inventory-list.xhtml',
					controller: 'IndexInventoryController'
				},
			}
		})
}]);