package rabbitsvsfoxes.Communication;

/**
 *
 * @author Georgi
 */
public enum MessageType {
    Inform, 
    AskObjectsNearby, 
    ReplyObjectsNearby,
    AskAgentNearby,
    ReplyAgentsNearby, 
    InformRabbitSpotted, 
    InformFoxSpotted, 
    RequestBackup;
    
}
