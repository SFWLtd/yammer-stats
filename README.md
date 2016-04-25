# yammer-stats [![Build Status](https://travis-ci.org/alondero/yammer-stats.svg?branch=master)](https://travis-ci.org/alondero/yammer-stats)
Provides some stats about a Yammer organisation.

## How to build and run
0. Clone repository
0. Run `gradle build`
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
