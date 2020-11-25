package nl.kingdev.graphqlclient.subscription;

import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.protocols.Protocol;

public class GraphQLDraft extends Draft_6455 {
    public GraphQLDraft() {
        getKnownProtocols().add(new Protocol("graphql-ws"));
    }
}
