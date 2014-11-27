branchManagementApp = angular.module('branchManagementApp');
branchManagementApp.controller('IndexBranchMemberController', ['$scope', '$state', '$stateParams', '$filter',  '$timeout', 'Branches', 'BranchUsers', 'Search', 'ngTableParams',
  function($scope, $state, $stateParams, $filter, $timeout, Branches, BranchUsers, Search, ngTableParams) {
	$scope.branch = {};

	$scope.displayBranchMemberDialog = function() {
		$scope.clearNotification();

		$('.branch-member-modal').modal('show');
	};

	$scope.refreshMembers = function() {
		$scope.startProgress();
		Branches.get({id: $stateParams.id},
			function(d, h){
				$scope.branch = d;
				$timeout(function(){ $scope.tableParams.reload(); $scope.endProgress();});
			},
			function(d, h) {
				$scope.endProgress();
			}
		);
	};

	$scope.tableParams = new ngTableParams(
		{ page: 1, count: 10, sorting: { id: 'asc' }},
		{
		    total: 0,
		    getData: function($defer, params) {
		    	if ($scope.branch.users.length > 0) {
			        var orderedData = params.sorting() ? $filter('orderBy')($scope.branch.users, params.orderBy()) : $scope.branch.users;

			        params.total(orderedData.length);
			        $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
		    	} else {
		    		$defer.reject();
		    	}
		    }
		});

    $scope.employeeTableParams = new ngTableParams(
		{ page: 1, count: 10, filter: {token: ''}},
		{
			total: 0,
			getData: function($defer, params) {
				$scope.startProgress();
				Search.get({entity: "user", token: params.filter().token, page: params.page() - 1, size: params.count()},
					function(d, h) {
						$scope.assignmentUsers = d.data;
						params.total(d.size);

						$scope.endProgress();
			            $defer.resolve($scope.assignmentUsers);
					}, function(d, h) {
						$scope.endProgress();
						$defer.reject();
					}
			    );
			}
		});

    $scope.isAssigned = function(user) {
    	var o = _.find($scope.branch.users, function(u) { return u.id == user.id });
    	return ! _.isUndefined(o);
    }

    $scope.searchEmployee = function() {
    	$scope.employeeTableParams.filter().token = $scope.searchText;
    	$scope.employeeTableParams.reload();
    };

    $scope.assignEmployee = function(user) {
    	$scope.startProgress();

    	var b = new BranchUsers();
    	b.$save({id: $scope.selectedBranch.id, userId: user.id},
    		function(d, h) {
    			$scope.refreshMembers();

    			$scope.clearNotification();
    			$scope.$parent.info = "Employee " + user.id + " has been assigned to this branch";

    			$scope.endProgress();
    		}, function(d, h) {
    			$scope.clearNotification();
    			$scope.$parent.errors = d.data;

    			$scope.endProgress();
    		}
    	);
    };

    $scope.unassignEmployee = function(user) {
    	$scope.startProgress();

    	var b = new BranchUsers();
    	BranchUsers.remove({id: $scope.selectedBranch.id, userId: user.id},
    		function(d, h) {
    			$scope.refreshMembers();

    			$scope.clearNotification();
    			$scope.$parent.info = "Employee " + user.id + " has been unassigned from this branch";

    			$scope.endProgress();
    		}, function(d, h) {
    			$scope.clearNotification();
    			$scope.$parent.errors = d.data;

    			$scope.endProgress();
    		}
    	);
    };

	$scope.refreshMembers();
  }
]);