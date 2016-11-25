select
  t1.id,
  t1.name,
  t2.age
from
  user_name t1, user_age t2
where
  t1.id = t2.id
