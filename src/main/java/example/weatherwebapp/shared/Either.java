package example.weatherwebapp.shared;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Either concept from functional programming.
 */
public final class Either<L, R> {

	private final Optional<L> left;
	private final Optional<R> right;

	private Either(Optional<L> left, Optional<R> right) {
		if (left.isEmpty() == right.isEmpty()) {
			throw new IllegalArgumentException("Either must have only 1 value (left or right).");
		}

		this.left = left;
		this.right = right;
	}

	/**
	 * Create a left Either.
	 * @param <L> Left type
	 * @param <R> Right type
	 * @param left Left instance to wrap in the Either.
	 * @return Either.
	 */
	public static <L, R> Either<L, R> left(L left) {
		return new Either<>(Optional.of(left), Optional.empty());
	}

	/**
	 * Create a right Either.
	 * @param <L> Left type
	 * @param <R> Right type
	 * @param right Right instance to wrap in the Either.
	 * @return Either.
	 */
	public static <L, R> Either<L, R> right(R right) {
		return new Either<>(Optional.empty(), Optional.of(right));
	}

	/**
	 * Return true if the current Either is a left.
	 * @return true if the current Either is a left.
	 */
	public boolean isLeft() {
		return left.isPresent();
	}

	/**
	 * Returns the left value of the Either <b>if the Either is a left</b>.
	 * @throws Throwable Thrown if the Either is NOT a left.
	 * @return Left value.
	 */
	public L left() {
		return left.orElseThrow();
	}

	/**
	 * Returns true if the current Either is a right.
	 * @return true if the current Either is a right.
	 */
	public boolean isRight() {
		return right.isPresent();
	}

	/**
	 * Returns the right value of the Either <b>if the Either is a right</b>.
	 * @throws Throwable Thrown if the Either is NOT a right.
	 * @return Right value.
	 */
	public R right() {
		return right.orElseThrow();
	}

	/**
	 * Convert the Either into a new value.
	 * @param <T> Type of the new value.
	 * @param onLeft Callback executed if the Either is a left.
	 * @param onRight Callback executed if the Either is a right.
	 * @return New value from the Either.
	 */
	public <T> T map(Function<L, T> onLeft, Function<R, T> onRight) {
		if (left.isPresent()) {
			Objects.requireNonNull(onLeft, "onLeft function is required on Left Either");
			return onLeft.apply(left.get());
		}

		Objects.requireNonNull(onRight, "onRight function is required on Right Either");
		return onRight.apply(right.get());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(left, right);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Either))
			return false;
		final Either other = (Either) obj;
		return Objects.equals(left, other.left) && Objects.equals(right, other.right);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{ left:").append(left).append(", right:").append(right).append(" }");
		return builder.toString();
	}

	
	
}
