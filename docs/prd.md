# Product Requirements Document (PRD): Rate My Duck

[Back to the Project 03 guide](../../../Users/paulc/Downloads/README.md)

> Copy this file to `docs/prd.md` in your team repo and replace everything in angle brackets.
> Keep it to **one page**. Commit it **before the group proposal presentation**; the commit date is checked.
> After the presentation, change it only through pull requests, so the history shows what changed and why.
> Your Final Submission is graded against this document.

**Team:** `<team name>` · **Repo:** `<link>` · **Last updated:** `<date>`

---

## 1. Users

Who is this for? Name **one** primary user type and describe them in one or two sentences.
"Everyone" and "students" are not user types; "a first-year student looking for a study group for
one specific course" is.

- **Primary user:** Someone who wants to rate and submit rubber ducks to be rated
- **Secondary user (optional):** Someone who wants to make sure that no inaproprate ducks can taint the pool of options

## 2. Problem

Two sentences: what the user struggles with today, and why existing options fall short.

`<problem statement>`
Rubber ducks are amazing beyond measure, letting us debug our code, bringing joy to our hearts, and any sane person has a large collection, but how can one empirically determine that their duck is the best rubber duck? Current mechanisms to rate ducks are either more broad ways to rate anything and thus not specialized, or are specialized in rating living ducks not the rubber ducks us programmers love.

## 3. Success measures

Three measurable signals that would show the product works. Each one needs a number and a way to
check it.

| # | Measure | Target | How we check it |
|---|---|---|---|
| S1 | Able to add a duck, and then see that duck | duck has a description, a live image, and a score of 0 | assert that the duck does not exist in the beginning, then assert that it does exist, has a score of zero, and that the link to the image is the same as the prepared live image |
| S1 | Able to delete inappropriate ducks | inappropriate duck no longer exists | checked by asserting that the prepared duck no longer exists |
| S1 | Able to rate a duck | after being rated negatively a duck with a rating of zero is -1, and after being rated positively it is 1 | checked by asserting the results from a baseline rateless duck |

## 4. Scope: MVP, vertical slice, and not building

**MVP** is the smallest set of stories that makes *this* product useful. It always includes the
account baseline (sign in with GitHub and Google, update profile, delete account) **plus** the
stories specific to your product.

Login
Add Duck
View Tier List

- **MVP stories:** `<PRD-1, PRD-2, ...>` (listed in section 5)
- **Vertical slice:** `<the one MVP story you will polish end to end and test with Playwright>`
- **Not building (this term):** Making other non duck tier lists, adding a way to upload a duck picture through downloads rather than using an already existing link, removing ducks due to bad ratings rather than individual admin decisions

## 5. User stories and acceptance criteria

One row per MVP story. Each GitHub issue links to its story ID (`PRD-3`) and each story has at
least one acceptance criterion in **Given / When / Then** form that someone outside the team could
test.

| ID | Story | Acceptance criteria |
|---|---|---|
| PRD-1 | As a duck connoisseur, I want to submit my duck to be rated. | **Given** The app functions, **when** users are able to add a duck, **then** add duck routes and ui are done |
| PRD-2 | As a duck connoisseur, I want to see all ducks | **Given** The app functions, **when** users are able to take quizzes both made by other players and some built-in quizzes, **then** the basic get path, and ui result is online |
| PRD-2 | As a duck connoisseur, I want to rate a duck | **Given** That you are able to see the ducks you want to rate, **when** someone is able to rate a duck negatively and positively with others seeing the results, **then** the duck rating functionality is complete. |
| PRD-3 | As a duck enthusiast, I want to be able to log in so that I can save the ducks I've submitted | **Given** The app functions, **when** users can log into their account, **then** login functionality is complete|

---

### Change log

| Date | PR | What changed and why |
|---|---|---|
| 10/29/26 | Initial version |
| 10/29/26 | PR1 | Updated to be about Rate My Duck |
