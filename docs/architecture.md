# System Architecture Layout
The C4 Model diagrams were created using structurizer, use the provided architexture.dsl file with the tool officially provided by structurizer.

The tool is dockerized and can be run locally, quick start guide can be found [here](https://docs.structurizr.com/local/quickstart).

## C4 Model
### System Context Diagram
![System Context Diagram](diagrams/SystemContext-dark.svg)
![System Context Diagram Key](diagrams/SystemContext-dark-key.svg)
### Container Diagram
![Container Diagram](diagrams/Containers-dark.svg)
![Container Diagram](diagrams/Containers-dark-key.svg)

## ERD
```mermaid
---
title: Rate My Duck Entity Relationship Diagram
---
erDiagram
    Ducks {
        int duck_id PK
        string name
        text image_link
        text description
        int creator_id FK
        int rating_score "Updated with every rating, or ever X amount of time"
        int position "Updated with every rating, or ever X amount of time"
    }
    
    User {
        int user_id PK
        string username
        text bio
        text profile_picture_link
        string provider "'github' or 'google'"
        int provider_user_id
        boolean is_admin
        string oauth_subject
    }
    
    Ratings {
        int user_id PK
        int duck_id PK
        boolean is_positive
    }
    
    Comments {
        int comment_id PK
        int user_id FK
        int duck_id FK
        text comment
    }
    
    Ducks |o--o{ Ratings : exists
    User |o--o{ Ducks : creates
    User |o--o{ Ratings: places
    User |o--o{ Comments: posts
    Ducks |o--o{ Comments: mentioned
```