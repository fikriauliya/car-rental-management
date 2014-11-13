myApp = angular.module('myApp');
myApp.controller('IndexUserController', ['$scope', 'Users', 'Organizations',
  function($scope, Users, Organizations) {
	$scope.newUser = new Users();
	$scope.currentPage = 0;
	$scope.totalPage = 1;
	$scope.units = [];

	$scope.createUser = function() {
		$scope.newUser.$save({},
			function(data, header) {
				$scope.refreshUsers();
				$scope.newUser = new Users();
				$scope.errors = "";
				$scope.info = "User created";
			},
			function(data, header) {
				$scope.errors = data.data;
				$scope.info = "";
			}
	   );
	};

	$scope.refreshUsers = function(page) {
		Users.query({page: page}, function(data, header) {
			$scope.users = data.users;
			$scope.currentPage = parseInt(data.currentPage);
			$scope.totalPage = parseInt(data.totalPage);
		});
	};

	$scope.nextPage = function() {
		$scope.refreshUsers($scope.currentPage + 1);
	};

	$scope.prevPage = function() {
		$scope.refreshUsers($scope.currentPage - 1);
	};

	$scope.refreshUnits = function() {
		Organizations.query({}, function(data, header) {
			$scope.units = [data];
			$scope.selectedUnit = data;
			console.log($scope.selectedUnit);
		});
	};

	$scope.changeUnit = function(branch) {
		$scope.selectedUnit = branch;
		console.log(branch);
	}

	$scope.refreshUsers(0);
	$scope.refreshUnits();
  }]
);