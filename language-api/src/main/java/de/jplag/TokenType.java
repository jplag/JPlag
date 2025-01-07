package de.jplag;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TokenType {
    private List<TokenAttribute> attributes;

    public TokenType(List<TokenAttribute> attributes) {
        this.attributes = attributes;
    }

    public TokenType(TokenAttribute attribute) {
        this(List.of(attribute));
    }

    public boolean isFileEnd() {
        return this.attributes.size() == 1 && this.attributes.getFirst() == SharedTokenAttribute.FILE_END;
    }

    public boolean matches(TokenAttribute... attributes) {
        return this.attributes.equals(List.of(attributes));
    }

    public List<TokenAttribute> getAttributes() {
        return this.attributes;
    }

    public boolean isExcludedFromMatching() {
        return this.attributes.stream().anyMatch(TokenAttribute::isExcludedFromMatching);
    }

    public TokenType constrained(Set<Object> contexts) {
        List<TokenAttribute> contained = this.attributes.stream().filter(it -> contexts.contains(it.getContext())).toList();
        if (contained.isEmpty()) {
            return this;
        } else {
            return new TokenType(contained);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        TokenType tokenType = (TokenType) o;
        return Objects.equals(attributes, tokenType.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(attributes);
    }

    @Override
    public String toString() {
        if (this.attributes.size() == 1) {
            return this.attributes.getFirst().getDescription();
        } else {
            return "{" + String.join(", ", this.attributes.stream().map(TokenAttribute::getDescription).toList()) + "}";
        }
    }
}
