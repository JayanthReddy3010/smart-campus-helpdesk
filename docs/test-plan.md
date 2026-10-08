# Smart Campus Helpdesk Test Plan

## Scope

This plan covers the Servlet/JSP, JDBC, session, authorization, dashboard, ticket workflow, comments, history, search/filtering, XML-independent database behavior, and security-sensitive navigation flows.

No functional test in the matrix below has been executed as part of creating this document. `Actual Result` is therefore `Not executed` and `Status` is `Not Run` unless a tester updates it with evidence.

## Test Environment

- Java 25 and Maven 3.9+
- MySQL 8.0+ with `database/schema.sql` applied
- Apache Tomcat/Jakarta Servlet 6 compatible container
- A configured `db.properties` or `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`
- Test accounts prepared through a controlled setup:
  - One `STUDENT`
  - One `STAFF`
  - One `ADMIN`
- At least two student accounts and two tickets owned by different users
- At least two active categories
- Browser with developer tools available

## Test Case Table

| Test Case ID | Feature | Input | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| REG-001 | Registration | Valid name, unique email, department, matching 8+ character passwords | Student account is created; password is stored as a hash; user is redirected to login | Not executed | Not Run |
| REG-002 | Registration | Duplicate email | Registration fails with a generic message; no second account is created | Not executed | Not Run |
| REG-003 | Registration | Passwords do not match | Registration fails; no database row is created | Not executed | Not Run |
| REG-004 | Registration validation | Blank name, email, or password | Registration fails with validation feedback | Not executed | Not Run |
| REG-005 | Registration validation | Invalid email, overlong name, or overlong department | Registration fails server-side | Not executed | Not Run |
| REG-006 | Registration authorization | Submit a role field such as `ADMIN` or `STAFF` manually | Account is still created only as `STUDENT` | Not executed | Not Run |
| LOG-001 | Login | Valid registered email and password | Session is created; user is redirected to the application | Not executed | Not Run |
| LOG-002 | Login | Invalid password | Generic authentication error; no session is created | Not executed | Not Run |
| LOG-003 | Login | Unknown email | Same generic authentication error as invalid password | Not executed | Not Run |
| LOG-004 | Login validation | Blank or malformed email/password | Request is rejected without database authentication | Not executed | Not Run |
| OUT-001 | Logout | Authenticated user submits POST logout | Session is invalidated and user is redirected to login | Not executed | Not Run |
| OUT-002 | Logout security | GET `/logout` | Request is rejected; session remains unchanged | Not executed | Not Run |
| SES-001 | Session handling | Login, then inspect session state | Only necessary user ID, name, email, and role are present; no password is stored | Not executed | Not Run |
| SES-002 | Session fixation | Record pre-login session ID, then log in | Session ID changes after successful login | Not executed | Not Run |
| SES-003 | Session expiry | Wait for configured timeout or expire session in container | Protected request requires login again | Not executed | Not Run |
| AUTH-001 | Role authorization | Student opens `/admin/dashboard` | Access is denied with HTTP 403 | Not executed | Not Run |
| AUTH-002 | Role authorization | Staff opens `/admin/tickets` | Access is denied with HTTP 403 | Not executed | Not Run |
| AUTH-003 | Role authorization | Unauthenticated user opens `/student/dashboard` | User is redirected to login | Not executed | Not Run |
| AUTH-004 | Unauthorized URL access | Change ticket ID in student details URL to another user's ticket | Request returns not found; other ticket data is not disclosed | Not executed | Not Run |
| AUTH-005 | Unauthorized POST | Non-admin posts to assignment/status endpoints | Operation is rejected; database is unchanged | Not executed | Not Run |
| TKT-001 | Ticket creation | Authenticated user submits valid category, subject, description, location, and priority | Ticket is created for the session user with status `OPEN`; generated ID is shown | Not executed | Not Run |
| TKT-002 | Ticket creation authorization | Unauthenticated POST to create-ticket endpoint | Request is rejected; no ticket is inserted | Not executed | Not Run |
| TKT-003 | Ticket validation | Missing category, subject, description, location, or priority | Server rejects the request; no ticket is inserted | Not executed | Not Run |
| TKT-004 | Ticket validation | Whitespace-only subject, description, or location | Server rejects the request; no ticket is inserted | Not executed | Not Run |
| TKT-005 | Ticket validation | Subject over 200, location over 255, description over 5,000 characters | Server rejects the request; no ticket is inserted | Not executed | Not Run |
| TKT-006 | Duplicate submission | Submit the same create form twice or replay its token | Only one ticket is created; second submission is rejected | Not executed | Not Run |
| TKT-007 | Ticket timestamps | Create a valid ticket | `created_at` and initial history timestamp are populated by the database | Not executed | Not Run |
| ASN-001 | Ticket assignment | Admin assigns an existing `STAFF` user to an `OPEN` ticket | `assigned_to` is stored; status becomes `ASSIGNED`; history is added | Not executed | Not Run |
| ASN-002 | Ticket assignment validation | Assign nonexistent user, student, or admin user | Assignment is rejected; ticket and history remain unchanged | Not executed | Not Run |
| ASN-003 | Ticket assignment validation | Assign nonexistent ticket | Request returns not found; no history row is added | Not executed | Not Run |
| ASN-004 | Assignment authorization | Student or staff posts assignment request | Request is rejected with no database change | Not executed | Not Run |
| ASN-005 | Assignment transaction | Force history insert failure after ticket update | Ticket assignment/status update rolls back | Not executed | Not Run |
| STS-001 | Status transition | `OPEN` to `ASSIGNED` | Transition succeeds and history records old/new status and actor | Not executed | Not Run |
| STS-002 | Status transition | `ASSIGNED` to `IN_PROGRESS` | Transition succeeds and history records the change | Not executed | Not Run |
| STS-003 | Status transition | `IN_PROGRESS` to `RESOLVED` | Transition succeeds and `resolved_at` is populated | Not executed | Not Run |
| STS-004 | Status transition | `RESOLVED` to `CLOSED` | Transition succeeds and history records the change | Not executed | Not Run |
| STS-005 | Invalid transition | `OPEN` to `RESOLVED`, `CLOSED` to `OPEN`, or any backward jump | Transition is rejected server-side; ticket remains unchanged | Not executed | Not Run |
| STS-006 | Status history | Perform multiple valid changes | History is chronological and includes actor, action, old status, new status, and timestamp | Not executed | Not Run |
| COM-001 | Comments | Authorized ticket owner submits a non-empty comment | Comment is inserted with ticket ID, session user ID, text, and database timestamp | Not executed | Not Run |
| COM-002 | Comments | Admin submits a comment on any ticket | Comment is inserted successfully | Not executed | Not Run |
| COM-003 | Comment validation | Empty, whitespace-only, or over-2,000-character comment | Comment is rejected; no row is inserted | Not executed | Not Run |
| COM-004 | Comment authorization | User submits comment for another user's ticket | Request returns not found; no comment is inserted | Not executed | Not Run |
| COM-005 | Comment display | Add several comments | Comments appear in chronological order and escaped content is rendered safely | Not executed | Not Run |
| HIS-001 | Ticket history | Open a ticket with no changes | Initial creation history is visible | Not executed | Not Run |
| HIS-002 | Ticket history authorization | Unauthorized user requests ticket details/history | Ticket and history are not disclosed | Not executed | Not Run |
| SEA-001 | Student/staff search | Search own ticket by exact ID | Matching owned ticket is returned | Not executed | Not Run |
| SEA-002 | Student/staff search | Search own tickets by partial subject | Matching owned tickets are returned; other users' tickets are excluded | Not executed | Not Run |
| SEA-003 | Admin search | Search by ticket ID or subject | Matching tickets across all users are returned to admin | Not executed | Not Run |
| FIL-001 | Student/staff filtering | Filter own tickets by each status | Only owned tickets with the selected status are returned | Not executed | Not Run |
| FIL-002 | Student/staff filtering | Filter by priority and category | Only owned tickets matching both filters are returned | Not executed | Not Run |
| FIL-003 | Admin filtering | Filter by status, priority, and category | Admin receives matching tickets across users | Not executed | Not Run |
| FIL-004 | Admin filtering | Filter by assigned staff | Only tickets assigned to that staff member are returned | Not executed | Not Run |
| FIL-005 | Filter injection resistance | Search value containing SQL syntax or wildcard characters | Query remains parameterized; no SQL error or unauthorized data appears | Not executed | Not Run |
| ADM-001 | Admin dashboard | Admin opens dashboard with known ticket data | Total, open, assigned, in-progress, resolved, closed, and high/urgent counts are correct | Not executed | Not Run |
| ADM-002 | Admin dashboard authorization | Student/staff opens admin dashboard | Access is denied | Not executed | Not Run |
| USR-001 | Student/staff dashboard | User opens dashboard | Welcome information and ticket counts reflect only that user's tickets | Not executed | Not Run |
| USR-002 | Empty state | User has no tickets | Dashboard and ticket list show a clear empty-state message | Not executed | Not Run |
| DB-001 | Database operations | Run schema against empty MySQL database | Tables, keys, constraints, indexes, and assignment status are created | Not executed | Not Run |
| DB-002 | Database operations | Create ticket with initial history | Ticket and history insert commit together | Not executed | Not Run |
| DB-003 | Database operations | Trigger a DAO SQL failure during a transaction | Transaction rolls back and no partial update remains | Not executed | Not Run |
| DB-004 | Database security | Inspect application logs during login/DB failure | Passwords, DB password, and connection credentials are absent | Not executed | Not Run |
| INV-001 | Invalid input | Negative/non-numeric IDs in ticket, assignment, or category parameters | Request is rejected or returns not found; no database mutation occurs | Not executed | Not Run |
| INV-002 | Invalid input | Unsupported enum values for status, priority, or role | Request is rejected; no mutation occurs | Not executed | Not Run |
| INV-003 | Invalid input | Oversized XML upload or invalid XML/XSD structure | Import is rejected without database changes | Not executed | Not Run |
| SEC-001 | Session expiry | Use browser back after session expiry | Protected content is not served from application cache; new request requires login | Not executed | Not Run |
| SEC-002 | Logout/back-button behavior | Logout, then use browser Back and refresh protected URL | Protected request is denied or redirected; cached sensitive pages are not reusable | Not executed | Not Run |
| SEC-003 | CSRF | POST a state-changing endpoint without or with an invalid CSRF token | Request is rejected with HTTP 403 | Not executed | Not Run |

## Manual Edge Cases

- Register emails differing only by case and verify the unique-email policy behaves consistently.
- Use leading/trailing whitespace around registration, login, ticket, and comment values.
- Use Unicode names, departments, subjects, locations, and comments.
- Use HTML and JavaScript payloads such as `<script>alert(1)</script>` in every text field and verify they render as text.
- Use very long Unicode input near each documented limit.
- Submit forms by pressing Enter, double-clicking submit, refreshing after POST, and replaying an old form token.
- Open multiple create-ticket tabs and submit forms from both tabs.
- Attempt direct access to JSP files and servlet URLs before and after login.
- Switch roles in a URL, query string, hidden field, or cookie and verify authorization still comes from the server session.
- Attempt to view, comment on, assign, or change status for another user's ticket.
- Try every invalid status transition, including repeated transitions and changes after `CLOSED`.
- Assign inactive/non-STAFF users, deleted users, and the current admin account.
- Test empty categories, inactive categories, deleted categories, and category imports containing duplicate names.
- Import XML with missing namespace, wrong version, missing fields, extra elements, external entities, DTDs, malformed encoding, and nested oversized content.
- Verify database outage behavior shows generic messages without stack traces, SQL, URLs, usernames, or passwords.
- Use multiple browser sessions to verify session isolation and logout in one browser does not authenticate another.
- Test back-button behavior after logout, session timeout, ticket creation, assignment, status update, and comment submission.
- Verify HTTP-to-HTTPS deployment sets the session cookie `Secure` flag at the container/proxy layer.

## Execution Notes

Update `Actual Result` with observed evidence and change `Status` to `Passed`, `Failed`, or `Blocked` only after executing each case. The existing automated build/test suite does not replace the database-backed browser and authorization scenarios in this plan.
