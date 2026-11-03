# Product Requirements Document (PRD): Rate My Duck

[Back to the Project 03 guide](../../../Users/paulc/Downloads/README.md)

**Team:** Rate My Duck · **Repo:** https://github.com/s1-p3-g2/s1_project3_group2 · **Last updated:** 11/2/2026

---

## 1. Users

- **Primary user:** Someone who wants to rate and submit rubber ducks to be rated
- **Secondary user (optional):** Someone who wants to make sure that no inaproprate ducks can taint the pool of options

## 2. Problem

Rubber ducks are amazing beyond measure, letting us debug our code, bringing joy to our hearts, and any sane person has a large collection, but how can one empirically determine that their duck is the best rubber duck? Current mechanisms to rate ducks are either more broad ways to rate anything and thus not specialized, or are specialized in rating living ducks not the rubber ducks us programmers love.

## 3. Success measures

| # | Measure | Target | How we check it |
|---|---|---|---|
| 1 | Able to add a duck, and then see that duck | duck has a description, a live image, and a score of 0 | assert that the duck does not exist in the beginning, then assert that it does exist, has a score of zero, and that the link to the image is the same as the prepared live image |
| 2 | Able to delete inappropriate ducks | inappropriate duck no longer exists | checked by asserting that the prepared duck no longer exists |
| 3 | Able to rate a duck | after being rated negatively a duck with a rating of zero is -1, and after being rated positively it is 1 | checked by asserting the results from a baseline rateless duck |

## 4. Scope: MVP, vertical slice, and not building

- **MVP stories:** `<PRD-1, PRD-2, ...>` (listed in section 5)
  Getting the basics of the login, adding, rating, and viewing ducks listed in more detail in the section below.
- **Vertical slice:** 

Login

Google OAuth

GitHub OAuth

Connect to the User data object and the API


Add Duck

Create add duck ui layout properly formatted to get the needed information

make the necessary post route in the api itself


View Tier List

create the ranked list of ducks based on score with the image through the link in the duck object visible and consistent in size with the other images

create the various get routes required to get this information


Rate Duck

add buttons to the viewable ducks to rate them, and a function to rate them without needing to go to the view page managed by someone else

add the patch routes to the api needed to change the ducks score value


- **Not building (this term):** Making other non duck tier lists, adding a way to upload a duck picture through downloads rather than using an already existing link, removing ducks due to bad ratings rather than individual admin decisions

## 5. User stories and acceptance criteria

| ID | Story | Acceptance criteria |
|---|---|---|
| https://github.com/s1-p3-g2/s1_project3_group2/issues/15 | As a duck connoisseur, I want to submit my duck to be rated. | **Given** The app functions, **when** users are able to add a duck, **then** add duck routes and ui are done |
| https://github.com/s1-p3-g2/s1_project3_group2/issues/16 | As a duck connoisseur, I want to see all ducks | **Given** The app functions, **when** users can navigate to the duck page and see all the ducks in the right order **then** the basic get path, and ui result is online |
| https://github.com/s1-p3-g2/s1_project3_group2/issues/17 | As a duck curator, I want to rate a duck | **Given** That you are able to see the ducks you want to rate, **when** someone is able to rate a duck negatively and positively with others seeing the results, **then** the duck rating functionality is complete. |
| https://github.com/s1-p3-g2/s1_project3_group2/issues/14 | As a duck enthusiast, I want to be able to log in so that I can save the ducks I've submitted | **Given** The app functions, **when** users can log into their account, **then** login functionality is complete|

---

### Change log

| Date | PR | What changed and why |
|---|---|---|
| 10/29/26 | Initial version |
| 11/2/26 | PR1 | Updated to be about Rate My Duck |
