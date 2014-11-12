myApp = angular.module('myApp');
myApp.controller('IndexUserController', ['$scope', 'Users',
  function($scope, Users) {
	$scope.newUser = new Users();

	$scope.createUser = function() {
		console.log($scope.newUser.firstName);
		$scope.newUser.$save({},
			function(data, header) {
				$scope.refreshUsers();
				$scope.newUser = new Users();
				$scope.errors = "";
				$scope.info = "User created";
			},
			function(data, header) {
				$scope.errors = data.data;
			}
	   );
	};

	$scope.refreshUsers = function() {
		Users.query({}, function(data, header) {
			$scope.users = data.user;
		});
	};

	$scope.refreshUsers();
  }]
);