# Be sure to restart your server when you modify this file.

# Your secret key for verifying cookie session data integrity.
# If you change this key, all old sessions will become invalid!
# Make sure the secret is at least 30 characters and all random, 
# no regular words or you'll be exposed to dictionary attacks.
ActionController::Base.session = {
  :key         => '_migrate_session',
  :secret      => '9a41691fa12532f5f8a2f6969abaf293a5b19b8fdfa6e65d181b222289930e307844e81986288a9853b0fb48dcbb73ddca69f31061e571dc516cc5029a88488c'
}

# Use the database for sessions instead of the cookie-based default,
# which shouldn't be used to store highly confidential information
# (create the session table with "rake db:sessions:create")
# ActionController::Base.session_store = :active_record_store
