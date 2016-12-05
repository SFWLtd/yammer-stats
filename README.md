# yammer-stats
Provides some stats about a Yammer organisation.

## How to build and run
0. Clone repository
0. Run `gradle build` (either using a local installation of Gradle or running the gradlew wrapper)
0. Get an application access token for your organisation and update the property 'yammer.accesstoken' (see application.properties.example)
0. Run the built jar file

## Retriving leaderboard of users with most liked posts
`curl localhost:8080\toplikes`

This should get you a response similar to:
[
  {
    "name": "Adam Londero",
    "likes": 1337
  }
]
