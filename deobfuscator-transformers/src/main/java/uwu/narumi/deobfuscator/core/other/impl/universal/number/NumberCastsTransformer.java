package uwu.narumi.deobfuscator.core.other.impl.universal.number;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.OriginalSourceValue;
import uwu.narumi.deobfuscator.api.asm.ClassWrapper;
import uwu.narumi.deobfuscator.api.helper.AsmHelper;
import uwu.narumi.deobfuscator.api.helper.AsmMathHelper;
import uwu.narumi.deobfuscator.api.transformer.FramedInstructionsTransformer;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Simplifies number casts on constant value.
 */
public class NumberCastsTransformer extends FramedInstructionsTransformer {
  @Override
  protected Stream<AbstractInsnNode> getInstructionsStream(Stream<AbstractInsnNode> stream) {
    return stream
            .filter(Objects::nonNull)
            .filter(insn -> AsmMathHelper.isNumberCast(insn.getOpcode()));
  }

  @Override
  protected boolean transformInstruction(ClassWrapper classWrapper, MethodNode methodNode, AbstractInsnNode insn, Frame<OriginalSourceValue> frame) {
    // Get instructions from stack that are passed
    OriginalSourceValue sourceValue = frame.getStack(frame.getStackSize() - 1);
    OriginalSourceValue originalSource = sourceValue.originalSource;
    if (!originalSource.isOneWayProduced()) return false;

    AbstractInsnNode valueInsn = originalSource.getProducer();

    if (valueInsn.isNumber()) {
      Number castedNumber = AsmMathHelper.castNumber(valueInsn.asNumber(), insn.getOpcode());

      methodNode.instructions.set(insn, AsmHelper.getNumber(castedNumber));
      methodNode.instructions.remove(sourceValue.getProducer());

      return true;
    }

    return false;
  }
}
