userServices = angular.module('userServices', ['ngResource']);
userServices.factory("Users",
  ["$resource",
   function($resource) {
	  return $resource(basePath + "/api/users/:id", {}, {
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

transferLogsServices = angular.module('transferLogsServices', ['ngResource']);
transferLogsServices.factory("TransferLogs",
  ["$resource",
   function($resource) {
	  return $resource(basePath + "/api/logs/transfers", {}, {
		  'query': {method:'GET', isArray:false},
	  });
   }
  ]);

peerReviewServices = angular.module('peerReviewServices', ['ngResource']);
peerReviewServices.factory("PeerReviews",
  ["$resource",
   function($resource) {
	  return $resource(basePath + "/api/reviews", {}, {
	  });
   }
  ]);

userAgendaServices = angular.module('userAgendaServices', ['ngResource']);
userAgendaServices.factory("UserAgendas",
  ["$resource",
   function($resource) {
	  return $resource(basePath + "/api/useragendas/:id", {}, {
	  });
   }
  ]);

branchServices = angular.module('branchServices', ['ngResource']);
branchServices.factory("Branches",
  ["$resource",
   function($resource) {
	  return $resource(basePath + "/api/branches/:id", {}, {
		  'update': { method:'PUT' }
	  });
   }
  ]);