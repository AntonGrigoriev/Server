select messages.* from messages, users where messages.user_id = users.id and users.name = 'Batman' and date_format(messages.date,'%Y-%m-%d')='2015-05-02';