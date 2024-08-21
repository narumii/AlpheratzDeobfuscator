package uwu.narumi.deobfuscator.api.transformer;

import java.util.List;
import java.util.function.Supplier;

import uwu.narumi.deobfuscator.api.asm.ClassWrapper;
import uwu.narumi.deobfuscator.api.context.Context;

public class ComposedTransformer extends Transformer {

  private final List<Supplier<Transformer>> transformers;

  @SafeVarargs
  public ComposedTransformer(Supplier<Transformer>... transformers) {
    this.transformers = List.of(transformers);
  }

  @SafeVarargs
  public ComposedTransformer(boolean rerunOnChange, Supplier<Transformer>... transformers) {
    this.transformers = List.of(transformers);
    this.rerunOnChange = rerunOnChange;
  }

  private boolean changed = false;

  @Override
  protected boolean transform(ClassWrapper scope, Context context) {
    transformers.forEach(transformerSupplier -> {
      changed |= Transformer.transform(transformerSupplier, scope, context);
    });

    return changed;
  }
}
