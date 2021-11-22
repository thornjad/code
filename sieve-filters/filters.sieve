# Apply label to all mail sent without TLS
require ["fileinto", "imap4flags"];

if header :is "x-pm-transfer-encryption" "none" {
  fileinto "unencrypted";
}


# Apply label to mail which was auto-forwarded from my university email
require "fileinto";

if header :is "X-Forwarded-For" "thorn399@umn.edu jmthornton@protonmail.com" {
  fileinto "thorn399";
}


# Apply label to mailing lists
require ["fileinto", "mailbox"];

if anyof(header "Precedence" "list", exists "List-Id") {
  fileinto "listserv";
}


# Outright reject unwanted emails.
# Note that `reject` is supposed to also `discard`, but I was having trouble getting this behavior
require ["reject", "imap4flags"];

if allof(address :is "From" "unwanted@example.com", address :is "From" "other-unwanted@example.com) {
  reject "Your message has been reject for unwanted content; If you believe this is in error, please send a new email from another email address with the subject Blacklisted mail problem";
  discard;
}