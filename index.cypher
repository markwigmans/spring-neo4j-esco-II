// create index
create index IF NOT EXISTS FOR (n:Entity) ON (n.conceptUri);

// Delete all
MATCH (n) DETACH DELETE n;