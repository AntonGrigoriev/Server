select messages.* from messages, users where messages.user_id = users.id and users.name = 'Batman';