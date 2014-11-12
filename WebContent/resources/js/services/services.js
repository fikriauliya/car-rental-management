userServices = angular.module('userServices', ['ngResource']);
userServices.factory("Users",
  ["$resource",
   function($resource) {
	  return $resource(basePath + "/api/users", {}, {
		  'query': {method:'GET', isArray:false}
	  });
   }
  ]);