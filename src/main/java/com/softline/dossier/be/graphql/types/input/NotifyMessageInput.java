package com.softline.dossier.be.graphql.types.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotifyMessageInput
{
    Long idComment;
    List<Long> agentIds;
}
