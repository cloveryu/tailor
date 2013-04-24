package com.github.tailor.inject;

/**
 * User: Clover Yu
 * Date: 4/24/13
 * Time: 2:26 PM
 */
public class Demand<T> {

    public Demand<T> from(Dependency<? super T> dependency) {
        return new Demand<T>(resource, dependency, serialNumber, cardinality);
    }

    public static <T> Demand<T> demand(Resource<T> resource, Dependency<? super T> dependency, int serialNumber, int cardinality) {
        return new Demand<T>(resource, dependency, serialNumber, cardinality);
    }

    private final Resource<T> resource;
    private final Dependency<? super T> dependency;
    private final int serialNumber;
    private final int cardinality;

    private Demand(Resource<T> resource, Dependency<? super T> dependency, int serialNumber,
                   int cardinality) {
        super();
        this.resource = resource;
        this.dependency = dependency;
        this.serialNumber = serialNumber;
        this.cardinality = cardinality;
    }

    public Dependency<? super T> getDependency() {
        return dependency;
    }

    public final int envSerialNumber() {
        return serialNumber;
    }

    public final int envCardinality() {
        return cardinality;
    }

    @Override
    public String toString() {
        return serialNumber + " " + dependency;
    }
}
