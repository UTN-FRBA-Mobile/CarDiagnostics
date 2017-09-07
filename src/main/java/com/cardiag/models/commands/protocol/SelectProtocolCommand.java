package com.cardiag.models.commands.protocol;

import com.cardiag.utils.enums.ObdProtocols;

/**
 * Select the protocol to use.
 *
 */
public class SelectProtocolCommand extends ObdProtocolCommand {

    private final ObdProtocols protocol;

    /**
     * <p>Constructor for SelectProtocolCommand.</p>
     *
     * @param protocol a {@link ObdProtocols} object.
     */
    public SelectProtocolCommand(final ObdProtocols protocol) {
        super("ATSP" + protocol.getValue());
        this.protocol = protocol;
    }


    @Override
    public String getFormattedResult() {
        return getResult();
    }


    @Override
    public String getName() {
        return "Select Protocol " + protocol.name();
    }

}
