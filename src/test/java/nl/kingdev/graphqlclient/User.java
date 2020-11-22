package nl.kingdev.graphqlclient;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class User {
    private String id, name, email;
}