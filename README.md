Dynamic Ride Sharing

The RideSharingApp has the below componenets:
1. Request :
   This has details like passengerId, contains pickup and drop off information(Events).
   It stores all the responses from the taxi in a response queue.
2. Taxi:
   Taxi  has details like the taxiId, no of occupied seats, and a temporary taxi schedule(queue) and a permanent taxi schedule (queue)
3. Event:
   The Request is divided into pick event and drop event .
   Events are actually pushed in the temporary and permanent schedule of the taxi.
   Event has locations(pick/drop), flag to see if it's pick or drop and timestamp value.
4. Response:
   Contains the corresponding request and taxi objects along with the cost and time.

The code starts in RideSharingApp class and each request is processed and scheduled in TaxiScheduling.
TaxiScheduling provides a response queue, which is sent to the PassengersEnd (The passenger picks the most cost effective taxi)
and sends a confirmation(YES) to that Taxi which finally adds it into it's permanent schedule queue.

Testing 

Some of the test data is present in the TestInputs folder within the source.

Dependencies :
For com.google.common.collect.ComparisonChain 
https://drive.google.com/file/d/1pLscQS2_nA2aKv0UMRH1d7Zmas39zs2T/view?usp=sharing
