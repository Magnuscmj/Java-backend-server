create table members_to_tasks(
    id serial primary key,
    member_id int, foreign key(member_id) references members(id),
    task_id int, foreign key(task_id) references project_tasks(id),
    constraint UC_link unique (member_id, task_id)
)