var mainApp = angular.module("cucumberFacetUI", []);
         
         mainApp.controller('featureController', function($scope, $http,$sce) {
           $scope.buildinProgress=false;
           $scope.pass=0;
           $scope.fail=0;
           $scope.skip=0;
             var sock = new SockJS('http://localhost:8080/socket');
             stompClient = Stomp.over(sock);
             stompClient.connect({}, onConnected, onError);
              function  onConnected() {
                 // Subscribe to the Public Topic
                 stompClient.subscribe('/chat', onMessageReceived);
             }
             function onError(error) {
                 console.log('Could not connect to WebSocket server. Please refresh this page to try again!');
             }
             
             function onMessageReceived(payload) {
                 var message = JSON.parse(payload.body);
                 if(message){
                     $scope.report=message;
                     var start=message.startTime;
                     var end=message.endTime;
                     updateReport();
                     if(start != null & end==null ) {
                      $scope.buildinProgress=true;
                      $scope.$apply();
                     }else if(start != null & end!=null ) {
                        $scope.buildinProgress=false;
                        $scope.$apply();
                     }
                 }
             }
             updateReport =function(){
              $scope.pass=0;
              $scope.fail=0;
              $scope.skip=0;
               $(".panel-group").empty();
              var output=$scope.report;
              var suites=output.suites;
              var suite=suites.suite;
              if(suite == null){return;}
              var tests=suite.tests;
              for (index = 0; index < tests.length; index++) { 
                var test=tests[index]; 
                var status=test.status; 
                var bdy='Test Name : '+test.name+' <br/> Test Start Time : '+test.startTime+'<br/>                Test End Time : '+test.endTime+'<br/>                Invocation count : '+test.invocationCount+'<br/>                Class : '+test.testClass+'<br/>                Arguments : '+test.parameters+'<br/>                Status : '+test.status+'<br/>                Success Percentage : '+test.successPercentage+'%<br/>';
                if(status == 1){
                  $scope.pass=$scope.pass+1;
                $( ".panel-group" ).append('<div class="panel panel-default"> <div class="panel-heading" role="tab"> <h4 class="panel-title"> <a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="true" aria-controls="collapseOne"> <i class="more-less"><span class="label label-primary">Pass</span></i> '+test.testClass+' : '+test.name+'  '+test.parameters+'</a> </h4> </div> <div id="collapseOne" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne"> <div class="panel-body"> '+bdy+' </div> </div> </div>'); 
                }
                if(status == 2){
                  $scope.fail=$scope.fail+1;
                  $( ".panel-group" ).append('<div class="panel panel-default"> <div class="panel-heading" role="tab"> <h4 class="panel-title"> <a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="true" aria-controls="collapseOne"> <i class="more-less"><span class="label label-danger">Fail</span></i> '+test.testClass+' : '+test.name+'  '+test.parameters+'</a> </h4> </div> <div id="collapseOne" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne"> <div class="panel-body"> '+bdy+' </div> </div> </div>'); 
                  }
              }              
             }
            $scope.isExecutionInProgress=function(){
              console.log("buildinProgress :"+$scope.buildinProgress);
              return $scope.buildinProgress;
            }
            $scope.startSuite = function(){
              var trustedUrl = $sce.trustAsResourceUrl('http://'+window.location.hostname+':8080/start');        
              $.ajax({
                type: "GET",
                url: trustedUrl,
                cache: false,
                async: false
            }).done(function(data) {
             // $scope.buildinProgress=data; 
                console.log("execution started inside:",+$scope.buildinProgress);
           }).fail(function(xhr)  {
            console.log("FAIL");
           });    
            }
         });
         
        
