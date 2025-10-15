


CREATE PROCEDURE spExeUpdateQueries(IN _schema VARCHAR(124))
BEGIN
 

    SET @sql = CONCAT("UPDATE ", _schema, ".agentstatech SET AgentState = 'LogOut' WHERE AgentState <> 'LogOut'");
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

    SET @sql = CONCAT("UPDATE ", _schema, ".agentstatus SET AgentState = 'LogOut' WHERE AgentState <> 'LogOut'");
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

    SET @sql = CONCAT("UPDATE ", _schema, ".aopsstatech SET AOPsState = 'Stop' WHERE AOPsState <> 'Stop'");
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

    SET @sql = CONCAT("UPDATE ", _schema, ".aopsstatus SET AOPsState = 'Stop' WHERE AOPsState <> 'Stop'");
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

    SET @sql = CONCAT("UPDATE ", _schema, ".agentaopsstch SET AgentAOPsState = 'Left' WHERE AgentAOPsState <> 'Left'");
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

    SET @sql = CONCAT("UPDATE ", _schema, ".agentqueuestatech SET AgentQueueState = 'Left' WHERE AgentQueueState <> 'Left'");
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
	
	 SET @sql = CONCAT("UPDATE ", _schema, ".agentqueuestatech SET AgentQueueState = 'Left' WHERE AgentQueueState <> 'Left'");
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;


END;
