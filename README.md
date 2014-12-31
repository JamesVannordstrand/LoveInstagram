#Pre-requirements
- Scala version 2.10.2

- Simple Build Tool (sbt)

- node package manager (npm)


##Startup
- cd LoveInstagramProxy
	- sbt run

- open new terminal

- cd LoveInstagram
	- npm start

- Go into your favorite browser and go to this URL (localhost:3000)

- Enter instagram app credentials and tags in photos that you wish to like

- Click LikePhotos and leave tab open while it runs.



##What's happening?
Every few minutes (defaulted to 3) it will use your instagram app credentials to like photos on your own behalf.
For every tag that you provided it will like 2 photos every 3 minutes. Instagram has a limit of 100 photos an hour. 
If on the "Liking Photos..." page you see "the limit for requests has been hit this hour" then instagram will not allow
anymore photos to be liked for this hour. You may leave the app running and it will recover after instagram allows more 
photos to be liked.