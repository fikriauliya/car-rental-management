userServices = angular.module('userServices', ['ngResource']);
userServices.factory("Users",
  ["$resource",
   function($resource) {
	  return $resource(basePath + "/api/users", {}, {
		  'query': {method:'GET', isArray:false},
	  	  'update': { method:'PUT' }
	  });
   }
  ]);

organizationServices = angular.module('organizationServices', ['ngResource']);
organizationServices.factory("Organizations",
  ["$resource",
   function($resource) {
	  return $resource(basePath + "/api/organizations", {}, {
		  'query': {method:'GET', isArray:false},
		  'update': { method:'PUT' }
	  });
   }
  ]);