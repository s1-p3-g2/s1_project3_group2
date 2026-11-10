workspace "Rate My Duck" "C4 architecture diagrams for the Rate My Duck platform" {
    model {
        # Users/Actors
        anonymous = person "Anonymous User" "Browses ducks and views ratings" {
            tags "Anonymous"
        }
        registered = person "Registered User" "Views and rates ducks" {
            tags "Registered"
        }
        admin = person "Administrator" "Moderates ratings and manages content" {
            tags "Administrator"
        }

        # Software System
        software = softwareSystem "Rate My Duck" "Web application for viewing and rating ducks" {
            ui = container "User Interface" "Web and mobile interfaces for browsing, rating, and moderation" "Web application" {
                tags "Frontend"
            }
            api = container "Backend API" "Provides duck information, ratings, reviews, and access control" "REST API" {
                tags "Backend"
            }
            db = container "Backend DB" "Stores ducks, ratings, reviews, and application data" "Database" {
                tags "Database"
            }
            static = container "Static Content" "HTML, CSS, JS, etc." {
                tags "Static"
             }
        }

        # Side Systems
        #   Broad Overview Auth for context diagram
        coreAuth = softwareSystem "OAuth / OIDC Provider" "Handles user sign-in, identity verification, and token issuance" {
            tags "Authentication"
        }

        #   Specific Oauth containers for container diagram
        github = softwareSystem "Github Oauth2" "Web-based Oauth Authentication Service" {
            tags "Authentication"
        }
        google = softwareSystem "Google Oauth2" "Mobile-based Oauth Authentication Service" {
            tags "Authentication"
        }

        # Relationships
        #   User Relationships
        anonymous -> software "Browses ducks"
        registered -> software "Views and rates ducks"
        admin -> software "Moderates users and ducks"

        # In-Depth User Relationships
        anonymous -> ui "Browses ducks and views ratings" "HTTPS"
        registered -> ui "Views ducks, submits ratings and reviews" "HTTPS"
        admin -> ui "Moderates reviews and manages content" "HTTPS"
        anonymous -> static "Loads User Interface" "HTTPS"
        registered -> static "Loads User Interface" "HTTPS"
        admin -> static "Loads User Interface" "HTTPS"

        #   Software Relationships
        ui -> api "Retrieves duck information and submits ratings; receives results" "HTTPS / JSON"
        api -> db "Reads and writes duck data, ratings, and reviews" "Database protocol"
        static -> ui "Delivers static content"

        # Basic Auth Relationships
        ui -> coreAuth "Signs users in and receives identity tokens" "OIDC / HTTPS"
        api -> coreAuth "Obtains token validation information and permissions when required" "OAuth 2.0 / OIDC"
        software -> coreAuth "Authenticates users and obtains authorization information" "OAuth 2.0 / OIDC"

        # In-Depth Auth Relationships
        ui -> github "Users on the Web Interface signs in and recieves identity tokens" "OIDC / HTTPS"
        ui -> google "Users on the Mobile Android Interface signs in and recieves identity tokens" "OIDC / HTTPS"
        github -> api "Uses recieved tokens to authenticate user and give roles" "OIDC / HTTPS"
        google -> api "Uses recieved tokens to authenticate user and give roles" "OIDC / HTTPS"
    }

    # Break whole diagram into different views, containing different layers of detail
    views {
        # C4 Context Diagram - Displays rough outline
        systemContext software "SystemContext" "Rate My Duck - System Context" {
            include anonymous registered admin software coreAuth
            autoLayout tb 250 220
        }

        # C4 Container Diagram - Displays more in-depth information on how information/app works
        container software "Containers" "Rate My Duck - Container Diagram" {
            include anonymous registered admin ui api db google github static
            autoLayout lr 250 180
        }

        styles {
            element "Anonymous" {
                background #64748b
                color #ffffff
                shape Person
            }
            element "Registered" {
                background #2563eb
                color #ffffff
                shape Person
            }
            element "Administrator" {
                background #7c3aed
                color #ffffff
                shape Person
            }
            element "Software System" {
                background #0f766e
                color #ffffff
            }
            element "Frontend" {
                background #2563eb
                color #ffffff
            }
            element "Backend" {
                background #0369a1
                color #ffffff
            }
            element "Database" {
                background #475569
                color #ffffff
                shape Cylinder
            }
            element "Authentication" {
                background #b45309
                color #ffffff
            }
            element "Static" {
                background #2563eb
                color #ffffff
            }
        }
    }
}
