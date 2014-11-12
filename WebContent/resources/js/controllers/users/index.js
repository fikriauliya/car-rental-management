myApp = angular.module('myApp');
myApp.controller('IndexUserController', ['$scope', 'Users',
  function($scope, Users) {
	$scope.newUser = new Users();
	$scope.currentPage = 0;
	$scope.totalPage = 1;

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
			if( Object.prototype.toString.call( data.users ) === '[object Array]' ) {
				$scope.users = data.users;
			} else {
				// stupid Jersey fix
				$scope.users = [data.users];
			}
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

	$scope.refreshUsers(0);
  }]
);